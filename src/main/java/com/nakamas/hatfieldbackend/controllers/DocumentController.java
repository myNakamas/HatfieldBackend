package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.outgoing.PdfAndImageDoc;
import com.nakamas.hatfieldbackend.services.DocumentService;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import com.nakamas.hatfieldbackend.services.InvoicingService;
import com.nakamas.hatfieldbackend.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/document")
public class DocumentController {
    @Value(value = "${fe-host:localhost:5173}")
    private String frontendHost;
    private final TicketService ticketService;
    private final InventoryItemService inventoryItemService;
    private final DocumentService documentService;
    private final InvoicingService invoiceService;

    //todo: Assign the qrcodes to redirect to the frontend page that shows data of the item.
    @PostMapping(value = "print/ticket", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printTicket(@RequestParam Long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        PdfAndImageDoc doc = documentService.createTicket("%s/tickets?ticketId=%s".formatted(frontendHost, ticket.getId()), ticket);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }
    @PostMapping(value = "print/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printInvoice(@RequestParam Long invoiceId) {
        Invoice invoice = invoiceService.getById(invoiceId);
        PdfAndImageDoc doc = documentService.createInvoice("%s/invoices/%s".formatted(frontendHost, invoice.getId()), invoice);
//        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @PostMapping(value = "print/tag/repair", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printRepairTag(@RequestParam Long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        PdfAndImageDoc doc = documentService.createRepairTag("%s/tickets?ticketId=%s".formatted(frontendHost, ticket.getId()), ticket);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @PostMapping(value = "print/tag/price", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printPriceTag(@RequestParam Long itemId) {
        InventoryItem item = inventoryItemService.getItem(itemId);
//        todo: add price to inventory item and pass inventory item object instead of its params
        PdfAndImageDoc doc = documentService.createPriceTag("%s/inventory?itemId=%s".formatted(frontendHost, item.getId()), item);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }
}
