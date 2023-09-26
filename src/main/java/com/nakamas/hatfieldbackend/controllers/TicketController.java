package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUsedItem;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.TicketFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView;
import com.nakamas.hatfieldbackend.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/ticket")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping("worker/create")
    public Long createTicket(@RequestBody CreateTicket ticket, @AuthenticationPrincipal User user) {
        return ticketService.createTicket(ticket, user).getId();
    }

    @GetMapping("byId")
    public TicketView getAllTickets(@RequestParam Long id) {
        return new TicketView(ticketService.getTicket(id));
    }

    @PutMapping("worker/update/{id}")
    public Long updateTicket(@RequestBody CreateTicket ticket, @PathVariable Long id) {
        return ticketService.update(ticket, id);
    }

    @GetMapping("all")
    public PageView<TicketView> getAllTickets(@AuthenticationPrincipal User user, TicketFilter ticketFilter, PageRequestView pageRequestView) {
        if (!user.getRole().equals(UserRole.ADMIN)) ticketFilter.setShopId(user.getShop().getId());
        return ticketService.findAll(ticketFilter, pageRequestView);
    }

    /**
     * @param user            The logged-in user
     * @param ticketFilter    The ticket filter
     * @param pageRequestView View containing the requested page and pageSize
     * @return all tickets that the logged user is assigned as client
     */
    @GetMapping("client/all")
    public PageView<TicketView> getAllTicketsForClient(@AuthenticationPrincipal User user, TicketFilter ticketFilter, PageRequestView pageRequestView) {
        ticketFilter.setShopId(user.getShop().getId());
        ticketFilter.setClientId(user.getId());
        return ticketService.findAll(ticketFilter, pageRequestView);
    }

    @GetMapping("active")
    public List<TicketView> getAllActiveTickets(TicketFilter ticketFilter) {
        return ticketService.findAllActive(ticketFilter);
    }

    @GetMapping("client/active")
    public List<TicketView> getAllActiveTicketsForClient(@AuthenticationPrincipal User user, TicketFilter ticketFilter) {
        ticketFilter.setShopId(user.getShop().getId());
        ticketFilter.setClientId(user.getId());
        return ticketService.findAllActive(ticketFilter);
    }

    @PutMapping("client/freeze")
    public void freezeTicket(@AuthenticationPrincipal User user, @RequestParam Long id) {
        ticketService.freezeRepair(user, id);
    }

    @PutMapping("client/cancel")
    public void cancelTicket(@AuthenticationPrincipal User user, @RequestParam Long id) {
        ticketService.cancelRepair(user, id);
    }

    @PutMapping("worker/start")
    public void startTicket(@AuthenticationPrincipal User user, @RequestParam Long id) {
        ticketService.startRepair(user, id);
    }

    @PutMapping("worker/complete")
    public void completeTicket(@AuthenticationPrincipal User user, @RequestParam Long id, @RequestParam Boolean success) {
        ticketService.completeRepair(user, id, success);
    }

    @PutMapping("worker/collected")
    public byte[] collectedDeviceAndCreateInvoice(@AuthenticationPrincipal User user, @RequestParam Long id, @RequestBody CreateInvoice invoice) {
        return ticketService.collectedDevice(user, id, invoice);
    }

    @PutMapping("worker/deposit")
    public byte[] createDepositInvoice(@AuthenticationPrincipal User user, @RequestParam Long id, @RequestBody CreateInvoice invoice) {
        return ticketService.createDepositInvoice(user, id, invoice);
    }

    @PostMapping("worker/part/use")
    public TicketView useItem(@RequestBody CreateUsedItem usedItem) {
        return new TicketView(ticketService.usePartFromInventory(usedItem.ticketId(), usedItem.itemId(), usedItem.count()));
    }
}
