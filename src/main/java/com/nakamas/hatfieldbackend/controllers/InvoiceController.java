package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InvoiceFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.reports.InvoiceReport;
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
    public void createNonRepairInvoice(@RequestBody CreateInvoice createInvoice, @AuthenticationPrincipal User user) {
        invoiceService.create(createInvoice, user);
    }

    @GetMapping("byId")
    public InvoiceView getById(@RequestParam Long invoiceId) {
        return new InvoiceView(invoiceService.getById(invoiceId));
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
        return invoiceService.getMonthlyReport(invoiceFilter);
    }

    @DeleteMapping("invalidate")
    public void invalidateInvoice(@RequestParam Long id) {
        invoiceService.invalidateInvoice(id);
    }
}
