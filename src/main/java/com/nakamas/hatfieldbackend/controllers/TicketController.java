package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/ticket")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    public Long createTicket(@RequestBody CreateTicket ticket , @AuthenticationPrincipal User user){
        return ticketService.createTicket(ticket,user).getId();
    }

//    todo: Post Start repair (move to lab, status started + message in chat + Open chat)
//    todo: Complete repair(move to? Status Completed, send notification)
//    todo: Mark as collected + chat message + generate invoice
}
