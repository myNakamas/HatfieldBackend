package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.DeviceLocation;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final DeviceLocationRepository deviceLocationRepository;
    private final InventoryItemService inventoryService;
    private final UserService userService;
    private final InvoiceService invoiceService;

    //region Main
    public Ticket createTicket(CreateTicket create, User loggedUser) {
        Ticket ticket = new Ticket(create, loggedUser);
        setOptionalProperties(create, ticket);
        if (create.clientId() != null) ticket.setClient(userService.getUser(create.clientId()));
        return ticketRepository.save(ticket);
    }

    public Long update(CreateTicket ticket, Long id) {
        Ticket ticketEntity = getTicket(id);
        ticketEntity.update(ticket);
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

    //        Must confirm the statuses
    public PageView<TicketView> findAllFinished(TicketFilter ticketFilter, PageRequestView pageRequestView) {
        ticketFilter.setTicketStatuses(List.of(TicketStatus.FINISHED, TicketStatus.COLLECTED, TicketStatus.SHIPPED_TO_CUSTOMER, TicketStatus.UNFIXABLE));
        Page<TicketView> page = ticketRepository.findAll(ticketFilter, pageRequestView.getPageRequest()).map(TicketView::new);
        return new PageView<>(page);
    }

    public PageView<TicketView> findAllOpen(TicketFilter ticketFilter, PageRequestView pageRequestView) {
        ticketFilter.setTicketStatuses(List.of(TicketStatus.STARTED, TicketStatus.WAITING_FOR_PARTS));
        Page<TicketView> page = ticketRepository.findAll(ticketFilter, pageRequestView.getPageRequest()).map(TicketView::new);
        return new PageView<>(page);
    }
    //endregion

    //region Ticket buttons

    public void setPriorityTo(Long id, Integer priority) {
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setPriority(priority);
        ticketRepository.save(ticket);
    }

    public void startRepair(User user, Long id){
        //use user to create log message
        Ticket ticket = ticketRepository.getReferenceById(id);
        //uses integer!!! make it flexible
        ticket.setDeviceLocation(deviceLocationRepository.findByName("at lab"));
        ticket.setStatus(TicketStatus.STARTED);
        //send message to client async?
        //send sms if options allow
        //to send email if options allow
        ticketRepository.save(ticket);
    }

    public void completeRepair(User user, Long id, Long locationId) {
        //use user to create log message
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setDeviceLocation(deviceLocationRepository.getReferenceById(locationId));
        ticket.setStatus(TicketStatus.FINISHED);
        //send message to client async?
        //send sms if options allow
        //to send email if options allow
        ticketRepository.save(ticket);
    }

    public void collectedDevice(User user, Long id, CreateInvoice invoice) {
        //use user to create log message
        Ticket ticket = ticketRepository.getReferenceById(id);
        ticket.setStatus(TicketStatus.COLLECTED);
        //send message to client
        invoiceService.create(invoice);
        ticketRepository.save(ticket);
        //maybe change return type if invoice creation is in BE
    }

    public Ticket usePartFromInventory(Long id, Long inventoryItemId, User user, int count) {
        Ticket ticket = getTicket(id);
        UsedPart usedPart = inventoryService.useItemForTicket(inventoryItemId, count, user);
        ticket.getUsedParts().add(usedPart);
        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new CustomException("Cannot find Ticket with selected ID"));
    }
    //endregion
}
