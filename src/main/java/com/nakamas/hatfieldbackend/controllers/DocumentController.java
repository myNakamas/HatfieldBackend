package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.services.DocumentService;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import com.nakamas.hatfieldbackend.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/document")
public class DocumentController {
    private final TicketService ticketService;
    private final InventoryItemService inventoryItemService;
    private final DocumentService documentService;

    //todo: Assign the qrcodes to redirect to the frontend page that shows data of the item.
    @PostMapping(value = "print/ticket", produces = MediaType.IMAGE_PNG_VALUE)
    private ResponseEntity<byte[]> printTicket(@RequestParam Long ticketId) throws IOException {
        Ticket ticket = ticketService.getTicket(ticketId);
//        todo: Add the initial username-password somewhere in the user to do easy access
        File image = documentService.createTicket(ticket.getClient().getEmail(), ticket);
        documentService.executePrint(image);
        byte[] bytes = Files.readAllBytes(image.toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }

    @PostMapping(value = "print/tag/repair", produces = MediaType.IMAGE_PNG_VALUE)
    private ResponseEntity<byte[]> printRepairTag(@RequestParam Long ticketId) throws IOException {
        Ticket ticket = ticketService.getTicket(ticketId);
//        todo: Add the initial username-password somewhere in the user to do easy access
        File image = documentService.createRepairTag(ticketId.toString(), ticket);
        documentService.executePrint(image);
        byte[] bytes = Files.readAllBytes(image.toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }

    @PostMapping(value = "print/tag/price", produces = MediaType.IMAGE_PNG_VALUE)
    private ResponseEntity<byte[]> printPriceTag(@RequestParam Long itemId) throws IOException {
        InventoryItem item = inventoryItemService.getItem(itemId);
//        todo: Add the initial username-password somewhere in the user to do easy access
//        todo: add price to inventory item and pass inventory item object instead of its params
        File image = documentService.createPriceTag(itemId.toString(), item.getBrand().getBrand(), item.getModel().getModel(), item.getOtherProperties().entrySet().stream().map(Objects::toString).collect(Collectors.toList()), 10f/*item.getPrice()*/);
        documentService.executePrint(image);
        byte[] bytes = Files.readAllBytes(image.toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }
}
