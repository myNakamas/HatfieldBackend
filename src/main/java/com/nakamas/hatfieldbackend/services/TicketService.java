package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.DeviceLocation;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.InvoiceType;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateChatMessage;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.TicketFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView;
import com.nakamas.hatfieldbackend.repositories.DeviceLocationRepository;
import com.nakamas.hatfieldbackend.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final DeviceLocationRepository deviceLocationRepository;
    private final InventoryItemService inventoryService;
    private final UserService userService;
    private final LoggerService loggerService;
    private final InvoicingService invoiceService;
    private final MessageService messageService;
    private final EmailService emailService;

    //region Main
    public Ticket createTicket(CreateTicket create, User loggedUser) {
        Ticket ticket = new Ticket(create, loggedUser);
        setOptionalProperties(create, ticket);
        if (create.clientId() != null) ticket.setClient(userService.getUser(create.clientId()));
        Ticket save = ticketRepository.save(ticket);
        loggerService.ticketActions(new Log(LogType.CREATED_TICKET), save);
        return save;
    }

    public Long update(CreateTicket ticket, Long id) {
        Ticket ticketEntity = getTicket(id);
        ticketEntity.update(ticket);
        if (ticket.clientId() != null) ticketEntity.setClient(userService.getUser(ticket.clientId()));
        setOptionalProperties(ticket, ticketEntity);
        loggerService.ticketActions(new Log(LogType.UPDATED_TICKET), ticketEntity);
        return ticketRepository.save(ticketEntity).getId();
    }
    //endregion

    //region Ticket population
    private void setOptionalProperties(CreateTicket create, Ticket ticket) {
        ticket.setDeviceBrand(inventoryService.getOrCreateBrand(create.deviceBrand()));
        if (ticket.getDeviceBrand() != null)
            ticket.setDeviceModel(inventoryService.getOrCreateModel(create.deviceModel(), ticket.getDeviceBrand()));
        if (!ticket.getDeviceLocationString().isBlank() && !ticket.getDeviceLocationString().equals(create.deviceLocation())) {
            loggerService.ticketActions(new Log(LogType.MOVED_TICKET), ticket);
        }
        DeviceLocation location = getOrCreateLocation(create.deviceLocation());
        if (location != null) {
            ticket.setDeviceLocation(location);
        }
    }

    public DeviceLocation getOrCreateLocation(String location) {
        if (location == null || location.isBlank()) return null;
        DeviceLocation existingByName = deviceLocationRepository.findByName(location);
        if (existingByName != null) return existingByName;
        return deviceLocationRepository.save(new DeviceLocation(location));
    }

    public PageView<TicketView> findAll(TicketFilter ticketFilter, PageRequestView pageRequestView) {
        Page<TicketView> page = ticketRepository.findAll(ticketFilter, pageRequestView.getPageRequest()).map(TicketView::new);
        return new PageView<>(page);
    }

    public List<TicketView> findAllActive(TicketFilter ticketFilter) {
        ticketFilter.setTicketStatuses(List.of(TicketStatus.STARTED, TicketStatus.DIAGNOSED, TicketStatus.PENDING));
        return ticketRepository.findAll(ticketFilter).stream().map(TicketView::new).toList();
    }

    //endregion

    //region Ticket buttons

    public void startRepair(User user, Long id) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setDeviceLocation(deviceLocationRepository.findByName("at lab"));
        ticket.setStatus(TicketStatus.STARTED);
        createMessageForTicket("Hello! The repair of your device has been initiated.", user, ticket);
        //send sms if options allow
        sendEmail(ticket.getClient(), "Update on Your Device Repair",
                "Dear client, \n\nYour device repair has begun. Updates coming soon. Please do check your chat in our system. \n\n Best regards, \n" + user.getShop().getShopName());
        loggerService.ticketActions(new Log(LogType.STARTED_TICKET), ticket);
        ticketRepository.save(ticket);
    }

    private void createMessageForTicket(String text, User user, Ticket ticket) {
        UUID clientId = ticket.getClient() != null ? ticket.getClient().getId() : null;
        if (clientId == user.getId()) {
            clientId = ticket.getCreatedBy().getId();
        }
        messageService.createMessage(new CreateChatMessage(text,
                ZonedDateTime.now(), user.getId(), clientId, ticket.getId(), false, true, null));
    }

    public void completeRepair(User user, Long id) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setStatus(TicketStatus.FINISHED);
        createMessageForTicket("Repairment actions have finished! Please come and pick " +
                               "up your device at a comfortable time.", user, ticket);
        //send sms if options allow
        sendEmail(ticket.getClient(), "Update on Your Device Repair",
                "Dear client, \n\nYour device repair has been completed. Please come pick up your device. \n\n Best regards, \n" + user.getShop().getShopName());
        loggerService.ticketActions(new Log(LogType.FINISHED_TICKET), ticket);
        ticketRepository.save(ticket);
    }

    public void freezeRepair(User user, Long id) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setStatus(TicketStatus.ON_HOLD);
        createMessageForTicket("Repairment actions are on hold!", user, ticket);
        //send sms if options allow
        sendEmail(ticket.getClient(), "Update on Your Device Repair",
                "Dear client, \n\nYour device repair has been frozen. Please contact us to resolve the issue. \n\n Best regards, \n" + user.getShop().getShopName());
        loggerService.ticketActions(new Log(LogType.FINISHED_TICKET), ticket);
        ticketRepository.save(ticket);
    }

    public void cancelRepair(User user, Long id) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setStatus(TicketStatus.CANCELLED_BY_CLIENT);
        createMessageForTicket("Repairment actions are canceled!", user, ticket);
        //send sms if options allow
        //email not needed - from client to client? xD
        loggerService.ticketActions(new Log(LogType.UPDATED_TICKET), ticket);
        ticketRepository.save(ticket);
    }

    public byte[] collectedDevice(User user, Long id, CreateInvoice invoice) {
        Ticket ticket = getTicket(id);
        ticket.setStatus(TicketStatus.COLLECTED);
        invoice.setType(InvoiceType.REPAIR);
        invoice.setTicketInfo(ticket);
        Invoice result = invoiceService.create(invoice, user);
        createMessageForTicket("The device has been collected. Information can be found" +
                               " in your 'invoices' tab. If that action hasn't been done by you please contact the store.", user, ticket);
        //sms
        sendEmail(ticket.getClient(), "Update on Your Device",
                "Dear client, \n\nYour device has been collected. Enjoy! \n\n Best regards, \n" + user.getShop().getShopName());
        ticketRepository.save(ticket);
        loggerService.ticketActions(new Log(LogType.COLLECTED_TICKET), ticket);
        return invoiceService.getAsBlob(result);
    }

    public Ticket usePartFromInventory(Long id, Long inventoryItemId, int count) {
        Ticket ticket = getTicket(id);
        UsedPart usedPart = inventoryService.useItemForTicket(inventoryItemId, ticket, count);
        ticket.getUsedParts().add(usedPart);
        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new CustomException("Cannot find Ticket with selected ID"));
    }
    //endregion

    private void sendSMS() {

    }

    private void sendEmail(User client, String title, String mailMessage) {
        if (client == null) return;
        ShopSettings shopSettings = client.getShop().getSettings();
        if (client.getEmailPermission() && client.getEmail() != null && client.getShop().getSettings().isEmailEnabled()) {
            emailService.sendMail(shopSettings.getGmail(), shopSettings.getGmailPassword(), client.getEmail(), title, mailMessage);
        }
    }
}
