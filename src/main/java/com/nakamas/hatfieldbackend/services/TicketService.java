package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.DeviceLocation;
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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
        if(location != null){ticket.setDeviceLocation(location);}
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

    public void updatePriority(Long id, Long newPositionId) {
        Ticket ticket = getTicket(id);
        Ticket newPositionTicket = getTicket(newPositionId);
        List<Ticket> tickets = new LinkedList<>(ticketRepository.findAll());
        int newIndex = tickets.indexOf(newPositionTicket);
        tickets.remove(ticket);
        tickets.add(newIndex, ticket);

        int newPriority = 0;
        for (Ticket t : tickets) {
            t.setPriority(newPriority++);
        }

        ticketRepository.saveAll(tickets);
    }

    public void startRepair(User user, Long id) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setDeviceLocation(deviceLocationRepository.findByName("at lab"));
        ticket.setStatus(TicketStatus.STARTED);
        createMessageForTicket("Hello! The repair of your device has been initiated.", user, ticket);
        //send sms if options allow
        //to send email if options allow
        loggerService.ticketActions(new Log(LogType.STARTED_TICKET), ticket);
        ticketRepository.save(ticket);
    }

    private void createMessageForTicket(String text, User user, Ticket ticket) {
        UUID clientId = ticket.getClient() != null ? ticket.getClient().getId() : null;
        messageService.createMessage(new CreateChatMessage(text,
                ZonedDateTime.now(), user.getId(), clientId, ticket.getId(), false, true, null));
    }

    public void completeRepair(User user, Long id, String location) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setDeviceLocation(getOrCreateLocation(location));
        ticket.setStatus(TicketStatus.FINISHED);
        createMessageForTicket("Repairment actions have finished! Please come and pick " +
                               "up your device at a comfortable time.", user, ticket);
        //send sms if options allow
        //to send email if options allow
        loggerService.ticketActions(new Log(LogType.FINISHED_TICKET), ticket);
        ticketRepository.save(ticket);
    }

    public byte[] collectedDevice(User user, Long id, CreateInvoice invoice) {
        Ticket ticket = getTicket(id);
        ticket.setStatus(TicketStatus.COLLECTED);
        invoice.setTicketInfo(ticket);
        invoice.setType(InvoiceType.REPAIR);
        Invoice result = invoiceService.create(invoice, user);
        createMessageForTicket("The device has been collected. Information can be found" +
                               " in your 'invoices' tab. If that action hasn't been done by you please contact the store.", user, ticket);
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
}
