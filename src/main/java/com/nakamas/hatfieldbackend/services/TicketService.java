package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final InventoryItemService inventoryService;
    private final UserService userService;


    public Ticket createTicket(CreateTicket create, User loggedUser) {
        Ticket ticket = new Ticket(create, loggedUser);
        if (create.deviceModel() != null)
            ticket.setDeviceModel(inventoryService.getOrCreateModel(create.deviceModel()));
        if (create.deviceBrand() != null)
            ticket.setDeviceBrand(inventoryService.getOrCreateBrand(create.deviceBrand()));
        if (create.clientId() != null)
            ticket.setClient(userService.getUser(create.clientId()));

        return ticketRepository.save(ticket);
    }
}
