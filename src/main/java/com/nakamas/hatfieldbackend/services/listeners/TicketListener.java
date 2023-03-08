package com.nakamas.hatfieldbackend.services.listeners;

import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import com.nakamas.hatfieldbackend.services.MessageService;
import com.nakamas.hatfieldbackend.services.TicketService;
import jakarta.persistence.PostPersist;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@NoArgsConstructor
@AllArgsConstructor
public class TicketListener {
    private MessageService messageService;
    private TicketService ticketService;
    private InventoryItemService inventoryService;

    @PostPersist
    private void afterTicketCreation(Ticket ticket){
        if(ticket.getId()!=null)
            log.info(ticket.getId().toString());
    }
}
