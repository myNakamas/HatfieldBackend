package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.outgoing.PdfAndImageDoc;
import com.nakamas.hatfieldbackend.services.DocumentService;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import com.nakamas.hatfieldbackend.services.InvoiceService;
import com.nakamas.hatfieldbackend.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/document")
public class DocumentController {
    private final TicketService ticketService;
    private final InventoryItemService inventoryItemService;
    private final DocumentService documentService;

    private final InvoiceService invoiceService;

    //todo: Assign the qrcodes to redirect to the frontend page that shows data of the item.
    @PostMapping(value = "print/ticket", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printTicket(@RequestParam Long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        PdfAndImageDoc doc = documentService.createTicket(ticket.getClient().getEmail(), ticket);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }
    @PostMapping(value = "print/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printInvoice(@RequestParam Long invoiceId) {
        Invoice invoice = invoiceService.getById(invoiceId);
//        todo: edit the qr information
        PdfAndImageDoc doc = documentService.createInvoice("QR", invoice);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @PostMapping(value = "print/tag/repair", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printRepairTag(@RequestParam Long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        PdfAndImageDoc doc = documentService.createRepairTag(ticketId.toString(), ticket);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @PostMapping(value = "print/tag/price", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printPriceTag(@RequestParam Long itemId) {
        InventoryItem item = inventoryItemService.getItem(itemId);
//        todo: add price to inventory item and pass inventory item object instead of its params
        List<String> properties = item.getOtherProperties().entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).toList();
        PdfAndImageDoc doc = documentService.createPriceTag(itemId.toString(), item.getBrand().getBrand(), item.getModel().getModel(), properties, 10f/*item.getPrice()*/);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }
}
