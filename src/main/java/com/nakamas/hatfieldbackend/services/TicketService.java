package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.DeviceLocation;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final DeviceLocationRepository deviceLocationRepository;
    private final InventoryItemService inventoryService;
    private final UserService userService;
    private final LoggerService loggerService;
    private final InvoiceService invoiceService;
    private final MessageService messageService;


    //region Main
    public Ticket createTicket(CreateTicket create, User loggedUser) {
        Ticket ticket = new Ticket(create, loggedUser);
        setOptionalProperties(create, ticket);
        if (create.clientId() != null) ticket.setClient(userService.getUser(create.clientId()));
        Ticket save = ticketRepository.save(ticket);
        loggerService.createLog("Ticket with id '%s' has been created by %s".formatted(save.getId(), loggedUser.getUsername()), loggedUser.getId(), save.getId());
        return save;
    }

    public Long update(CreateTicket ticket, Long id) {
        Ticket ticketEntity = getTicket(id);
        ticketEntity.update(ticket);
        if (ticket.clientId() != null) ticketEntity.setClient(userService.getUser(ticket.clientId()));
        setOptionalProperties(ticket, ticketEntity);
        return ticketRepository.save(ticketEntity).getId();
    }
    //endregion

    //region Ticket population
    private void setOptionalProperties(CreateTicket create, Ticket ticket) {
        ticket.setDeviceModel(inventoryService.getOrCreateModel(create.deviceModel()));
        ticket.setDeviceBrand(inventoryService.getOrCreateBrand(create.deviceBrand()));
        ticket.setDeviceLocation(getOrCreateLocation(create.deviceBrand()));
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
//        todo: filter all active
        return ticketRepository.findAll(ticketFilter).stream().map(TicketView::new).toList();
    }

    //endregion

    //region Ticket buttons

    public void setPriorityTo(Long id, Integer priority) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setPriority(priority);
        ticketRepository.save(ticket);
    }

    public void startRepair(User user, Long id) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setDeviceLocation(deviceLocationRepository.findByName("at lab"));
        ticket.setStatus(TicketStatus.STARTED);
        messageService.createMessage(new CreateChatMessage("Hello! The repair of your device has been initiated.",
                LocalDateTime.now(), user.getId(), ticket.getClient().getId(), ticket.getId(), null));
        //send sms if options allow
        //to send email if options allow
        loggerService.createLog("Repair on ticket '%s' was started by %s".formatted(id, user.getUsername()), user.getId(), id);
        ticketRepository.save(ticket);
    }

    public void completeRepair(User user, Long id, String location) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setDeviceLocation(getOrCreateLocation(location));
        ticket.setStatus(TicketStatus.FINISHED);
        messageService.createMessage(new CreateChatMessage("Repairment actions have finished! Please come and pick " +
                                                           "up your device at a comfortable time.",
                LocalDateTime.now(), user.getId(), ticket.getClient().getId(), ticket.getId(), null));
        //send sms if options allow
        //to send email if options allow
        loggerService.createLog("The repair has been completed by " + user.getUsername(), user.getId(), id);
        ticketRepository.save(ticket);
    }

    public byte[] collectedDevice(User user, Long id, CreateInvoice invoice) {
        Ticket ticket = getTicket(id);
        ticket.setStatus(TicketStatus.COLLECTED);
        invoice.setTicketInfo(ticket);
        invoice.setCreatedBy(user);
        Invoice result = invoiceService.create(invoice);
        messageService.createMessage(new CreateChatMessage("The device has been collected. Information can be found" +
                                                           " in your 'invoices' tab. If that action hasn't been done by you please contact the store.",
                LocalDateTime.now(), user.getId(), ticket.getClient().getId(), ticket.getId(), null));
        ticketRepository.save(ticket);
        loggerService.createLog("The device has been marked as collected by " + user.getUsername(), user.getId(), id);
        return invoiceService.getAsBlob(result);
    }

    public Ticket usePartFromInventory(Long id, Long inventoryItemId, int count, User user) {
        Ticket ticket = getTicket(id);
        UsedPart usedPart = inventoryService.useItemForTicket(inventoryItemId, ticket, count);
        ticket.getUsedParts().add(usedPart);
        loggerService.createLogUsedItem(usedPart,id, user);
        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new CustomException("Cannot find Ticket with selected ID"));
    }
    //endregion
}
