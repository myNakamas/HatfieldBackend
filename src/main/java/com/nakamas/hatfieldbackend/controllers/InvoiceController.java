package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.InvoiceView;
import com.nakamas.hatfieldbackend.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping("create")
    public void createNonRepairInvoice( CreateInvoice createInvoice){
        invoiceService.create(createInvoice);
    }

    @GetMapping("byId")
    public InvoiceView getById(@RequestParam Long invoiceId){
        return new InvoiceView(invoiceService.getById(invoiceId));
    }

    @GetMapping("byTicketId")
    public InvoiceView getByTicketId(@RequestParam Long ticketId){
        return new InvoiceView(invoiceService.getByTicketId(ticketId));
    }

    @GetMapping("allByClient")
    public List<InvoiceView> getAllByClient(@RequestParam UUID clientId){
        return invoiceService.getByClientId(clientId).stream().map(InvoiceView :: new).toList();
    }

    @GetMapping("all")//to be connected to a filter
    public List<InvoiceView> getAllInvoices(){
        return invoiceService.getAll().stream().map(InvoiceView :: new).toList();
    }
}
