package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.config.exception.ForbiddenActionException;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.outgoing.PdfAndImageDoc;
import com.nakamas.hatfieldbackend.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/document")
public class DocumentController {
    @Value(value = "${fe-host:http://localhost:5173}")
    private String frontendHost;
    private final TicketService ticketService;
    private final InventoryItemService inventoryItemService;
    private final DocumentService documentService;
    private final InvoicingService invoiceService;
    private final UserService userService;

    //todo: Assign the QR codes to redirect to the frontend page that shows data of the item.
    @PostMapping(value = "print/ticket", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printTicket(@RequestParam Long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        PdfAndImageDoc doc = documentService.createTicket(ticket);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }
    @GetMapping(value = "print/ticket", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> previewPrintTicket(@RequestParam Long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        PdfAndImageDoc doc = documentService.createTicket(ticket);
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @PostMapping(value = "print/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printInvoice(@RequestParam Long invoiceId) {
        Invoice invoice = invoiceService.getById(invoiceId);
        byte[] bytes = documentService.createInvoice("%s/invoices?invoiceId=%s".formatted(frontendHost, invoice.getId()), invoice);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @GetMapping(value = "client/print/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printClientInvoice(@AuthenticationPrincipal User user, @RequestParam Long invoiceId) {
        Invoice invoice = invoiceService.getById(invoiceId);
        if (user == null) throw new CustomException("No user with session");
        if (invoice.getClient() == null || invoice.getClient().getId() == null || !invoice.getClient().getId().equals(user.getId()))
            throw new ForbiddenActionException("You cannot print this invoice");
        byte[] bytes = documentService.createInvoice("%s/invoices?invoiceId=%s".formatted(frontendHost, invoice.getId()), invoice);
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
    @GetMapping(value = "print/tag/repair", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> previewPrintRepairTag(@RequestParam Long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        PdfAndImageDoc doc = documentService.createRepairTag("%s/tickets?ticketId=%s".formatted(frontendHost, ticket.getId()), ticket);
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @PostMapping(value = "print/tag/user", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printRepairTag(@RequestParam UUID userId) {
        User user = userService.getUser(userId);
        PdfAndImageDoc doc = documentService.createUserTag(frontendHost, user);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @GetMapping(value = "print/tag/user", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> previewRepairTag(@RequestParam UUID userId) {
        User user = userService.getUser(userId);
        PdfAndImageDoc doc = documentService.createUserTag(frontendHost, user);
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @PostMapping(value = "print/tag/price", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> printPriceTag(@RequestParam Long itemId) {
        InventoryItem item = inventoryItemService.getItem(itemId);
        PdfAndImageDoc doc = documentService.createPriceTag("%s/inventory?itemId=%s".formatted(frontendHost, item.getId()), item);
        documentService.executePrint(doc.image());
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @GetMapping(value = "print/tag/price", produces = MediaType.APPLICATION_PDF_VALUE)
    private ResponseEntity<byte[]> previewPriceTag(@RequestParam Long itemId) {
        InventoryItem item = inventoryItemService.getItem(itemId);
        PdfAndImageDoc doc = documentService.createPriceTag("%s/inventory?itemId=%s".formatted(frontendHost, item.getId()), item);
        byte[] bytes = doc.pdfBytes();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }
}
