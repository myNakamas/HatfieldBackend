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
import com.nakamas.hatfieldbackend.models.views.outgoing.PdfAndImageDoc;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView;
import com.nakamas.hatfieldbackend.repositories.DeviceLocationRepository;
import com.nakamas.hatfieldbackend.repositories.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    private static final String START_REPAIR_CHAT_MESSAGE = "Repair in Progress.\nWe're working on your device. Updates coming soon!";
    private static final String SUCCESS_REPAIR_CHAT_MESSAGE = "Repair Completed!\nYour device is ready for pickup at your convenience.";
    private static final String FAILED_REPAIR_CHAT_MESSAGE = "Repair Unsuccessful.\nUnfortunately, your device cannot be repaired.\nPlease come and pick it up at your earliest convenience.";

    private final TicketRepository ticketRepository;
    private final DeviceLocationRepository deviceLocationRepository;
    private final InventoryItemService inventoryService;
    private final UserService userService;
    private final LoggerService loggerService;
    private final InvoicingService invoiceService;
    private final MessageService messageService;
    private final EmailService emailService;
    private final SmsService smsService;
    private final TemplateEngine templateEngine;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


    private final DocumentService documentService;

    //region Main
    public Ticket createTicket(CreateTicket create, User loggedUser) {
        Ticket ticket = new Ticket(create, loggedUser);
        setOptionalProperties(create, ticket);
        if (create.clientId() != null) ticket.setClient(userService.getUser(create.clientId()));
        Ticket save = ticketRepository.save(ticket);
        sendInitialTicketMessage(loggedUser, ticket);
//        printTicketLabels(save);
        loggerService.createLog(new Log(save.getId(), LogType.CREATED_TICKET), save.getId());
        return save;
    }

    private void sendInitialTicketMessage(User loggedUser, Ticket ticket) {
        StringBuilder stringBuilder = new StringBuilder("Hello! We created Ticket#%s for you. \n".formatted(ticket.getId()));
        if (!ticket.getDeviceProblemExplanation().isBlank())
            stringBuilder.append("\nTicket description:").append(ticket.getDeviceProblemExplanation());
        if (!ticket.getCustomerRequest().isBlank())
            stringBuilder.append("\nAdditional request:").append(ticket.getDeviceProblemExplanation());
        createMessageForTicket(stringBuilder.toString(), loggedUser, ticket);
    }

    public Ticket update(CreateTicket ticket, Long id) {
        Ticket ticketEntity = getTicket(id);
        String updateInfo = loggerService.ticketUpdateCheck(ticketEntity, ticket);
        ticketEntity.update(ticket);
        if (ticket.clientId() != null) ticketEntity.setClient(userService.getUser(ticket.clientId()));
        setOptionalProperties(ticket, ticketEntity);
        loggerService.createLog(new Log(ticketEntity.getId(), LogType.UPDATED_TICKET), Objects.requireNonNull(ticketEntity.getId()).toString(), updateInfo);
        return ticketRepository.save(ticketEntity);
    }
    //endregion

    //region Ticket population
    private void setOptionalProperties(CreateTicket create, Ticket ticket) {
        if (create.deviceBrand() != null)
            ticket.setDeviceBrand(inventoryService.getOrCreateBrand(create.deviceBrand()));
        if (create.deviceModel() != null)
            ticket.setDeviceModel(inventoryService.getOrCreateModel(create.deviceModel(), ticket.getDeviceBrand()));
        if (create.deviceLocation() != null) {
            DeviceLocation location = getOrCreateLocation(create.deviceLocation());
            if (location != null) {
                ticket.setDeviceLocation(location);
            }
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
        if (ticketFilter.getTicketStatuses() == null || ticketFilter.getTicketStatuses().isEmpty())
            ticketFilter.setTicketStatuses(List.of(TicketStatus.STARTED, TicketStatus.DIAGNOSED, TicketStatus.PENDING, TicketStatus.FINISHED));
        return ticketRepository.findAll(ticketFilter).stream().map(TicketView::new).toList();
    }

    //endregion

    //region Ticket buttons
    public void startRepair(User user, Long id) {
        Ticket ticket = getTicket(id);
        ticket.setDeviceLocation(deviceLocationRepository.findByName("IN_THE_LAB"));
        ticket.setStatus(TicketStatus.STARTED);
        createMessageForTicket(START_REPAIR_CHAT_MESSAGE, user, ticket);
        loggerService.createLog(new Log(ticket.getId(), LogType.STARTED_TICKET), ticket.getId());
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

    public void completeRepair(User user, Long id, Boolean success) {
        Ticket ticket = getTicket(id);
        if (success)
            completeSuccessfulRepair(user, ticket);
        else completeUnfixableRepair(user, ticket);
        ticketRepository.save(ticket);
    }

    private void completeSuccessfulRepair(User user, Ticket ticket) {
        ticket.setStatus(TicketStatus.FINISHED);
        createMessageForTicket(SUCCESS_REPAIR_CHAT_MESSAGE, user, ticket);
        sendEmailOrSms(ticket.getClient(), ticket, "email/ticketCompletedSuccess", "ticketCompletedSuccess.txt", "Your Device Repair has been completed!");

        loggerService.createLog(new Log(ticket.getId(), LogType.FINISHED_TICKET), ticket.getId());
    }

    private void completeUnfixableRepair(User user, Ticket ticket) {
        ticket.setStatus(TicketStatus.UNFIXABLE);
        createMessageForTicket(FAILED_REPAIR_CHAT_MESSAGE, user, ticket);
        sendEmailOrSms(ticket.getClient(), ticket, "email/ticketCompletedFail", "ticketCompletedFail.txt", "Unsuccessful repair.");

        loggerService.createLog(new Log(ticket.getId(), LogType.FINISHED_TICKET), ticket.getId());
    }

    public void freezeRepair(User user, Long id) {
        Ticket ticket = getTicket(id);
        ticket.setStatus(TicketStatus.ON_HOLD);
        createMessageForTicket("Repairment actions are on hold!", user, ticket);
        loggerService.createLog(new Log(ticket.getId(), LogType.UPDATED_TICKET), Objects.requireNonNull(ticket.getId()).toString(), "Status updated to FROZEN.");
        ticketRepository.save(ticket);
    }

    public void cancelRepair(User user, Long id) {
        Ticket ticket = getTicket(id);
        ticket.setStatus(TicketStatus.CANCELLED_BY_CLIENT);
        createMessageForTicket("Repairment actions are canceled!", user, ticket);
        loggerService.createLog(new Log(ticket.getId(), LogType.UPDATED_TICKET), Objects.requireNonNull(ticket.getId()).toString(), "Status updated to CANCELLED.");
        ticketRepository.save(ticket);
    }

    public byte[] collectedDevice(User user, Long id, CreateInvoice invoice) {
        Ticket ticket = getTicket(id);
        ticket.setStatus(TicketStatus.COLLECTED);
        invoice.setType(InvoiceType.REPAIR);
        invoice.setTicketInfo(ticket);
        //        todo: Invalidate all previous Deposit invoices for the selected ticket
        Invoice result = invoiceService.create(invoice, user);
        createMessageForTicket("The device has been collected. Information can be found" +
                " in your 'invoices' tab. If that action hasn't been done by you please contact the store.", user, ticket);
        ticketRepository.save(ticket);
        loggerService.createLog(new Log(ticket.getId(), LogType.COLLECTED_TICKET), ticket.getId());
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
    @Transactional
    public TicketView toTicketView(Ticket ticket) {
        return new TicketView(ticket);
    }

    //endregion
    private void sendEmailOrSms(User client, Ticket ticket, String emailTemplate, String smsTemplate, String title) {
        if (emailService.isEmailEnabled(client)) {
            String messageBody = templateEngine.process(emailTemplate, getTicketContext(ticket));
            emailService.sendMail(client, messageBody, title);
        } else if (!smsTemplate.isBlank()) {
            boolean result = smsService.sendSms(client, smsTemplate, getTicketContext(ticket));
            if (!result)
                throw new CustomException(HttpStatus.OK, "The client could not be reached through email or sms. Please check their settings or the shop's settings.");
        }
    }

    public Context getTicketContext(Ticket ticket) {
        Context context = new Context();
        context.setVariable("ticket", ticket);
        context.setVariable("client", ticket.getClient());
        Optional<Invoice> ticketInvoiceOptional = ticket.getInvoices().stream().filter(Invoice::isTicketInvoice).findFirst();
        if (ticketInvoiceOptional.isPresent()) {
            Invoice invoice = ticketInvoiceOptional.get();
            context.setVariable("invoice", invoice);
        }
        context.setVariable("deadline", ticket.getDeadline().format(formatter));
        return context;
    }

    public void printTicketLabels(Ticket ticket) {
        try {
            printTicket(ticket);
            printTicketTag(ticket);
            if (ticket.getAccessories().contains("With Charger,")) {
                printTicketTag(ticket);
            }
        } catch (CustomException e) {
            log.warn(e.getMessage());
        }
    }

    public void printTicket(Ticket ticket) throws CustomException {
        if (ticket.getShop().getSettings().isPrintEnabled()) {
            PdfAndImageDoc doc = documentService.createTicket(ticket);
            documentService.executePrint(doc.image());
        }

    }

    public void printTicketTag(Ticket ticket) throws CustomException {
        if (ticket.getShop().getSettings().isPrintEnabled()) {
            PdfAndImageDoc doc = documentService.createRepairTag("%s/tickets?ticketId=%s".formatted(documentService.getFrontendHost(), ticket.getId()), ticket);
            documentService.executePrint(doc.image());
        }
    }

    public byte[] createDepositInvoice(User user, Long id, CreateInvoice invoice) {
        Ticket ticket = getTicket(id);
        ticket.setDeposit(invoice.getTotalPrice());
        ticketRepository.save(ticket);
        invoice.setType(InvoiceType.DEPOSIT);
        invoice.setTicketInfo(ticket);
        Invoice result = invoiceService.create(invoice, user);
//        todo: Invalidate all previous Deposit invoices for the selected ticket
        return invoiceService.getAsBlob(result);
    }
}
