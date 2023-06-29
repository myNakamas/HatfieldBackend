package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.config.exception.ForbiddenActionException;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InvoiceFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.reports.InvoiceReport;
import com.nakamas.hatfieldbackend.models.views.outgoing.reports.SellReport;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.InvoiceView;
import com.nakamas.hatfieldbackend.services.InvoicingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoicingService invoiceService;

    @PostMapping("create")
    public Long createNonRepairInvoice(@RequestBody CreateInvoice invoice, @AuthenticationPrincipal User user) {
        return invoiceService.create(invoice, user).getId();
    }

    @GetMapping("byId")
    public InvoiceView getById(@AuthenticationPrincipal User user, @RequestParam Long invoiceId) {
        if (user == null || user.getRole() == null) throw new CustomException("Invalid session or logged user data.");
        InvoiceView invoiceView = new InvoiceView(invoiceService.getById(invoiceId));
        if (user.getRole().equals(UserRole.CLIENT) && !invoiceView.client().userId().equals(user.getId()))
            throw new ForbiddenActionException("You have no rights to viewing this invoice");
        return invoiceView;
    }

    @GetMapping("byTicketId")
    public InvoiceView getByTicketId(@RequestParam Long ticketId) {
        return new InvoiceView(invoiceService.getByTicketId(ticketId));
    }

    @GetMapping("allByClient")
    public List<InvoiceView> getAllByClient(@RequestParam UUID clientId) {
        return invoiceService.getByClientId(clientId).stream().map(InvoiceView::new).toList();
    }

    @GetMapping("all")
    public Page<InvoiceView> getAllInvoices(InvoiceFilter invoiceFilter, PageRequestView pageRequestView) {
        return invoiceService.getAll(invoiceFilter, pageRequestView).map(InvoiceView::new);
    }

    @GetMapping("report")
    public InvoiceReport getMonthlyReport(InvoiceFilter invoiceFilter) {
        return invoiceService.getInvoiceMonthlyReport(invoiceFilter);
    }

    @GetMapping("report/sell")
    public SellReport getSellReport(InvoiceFilter invoiceFilter) {
        return invoiceService.getSellReport(invoiceFilter);
    }

    @DeleteMapping("invalidate")
    public void invalidateInvoice(@RequestParam Long id) {
        invoiceService.invalidateInvoice(id);
    }
}
