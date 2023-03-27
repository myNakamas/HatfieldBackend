package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final DocumentService documentService;
    private final InvoiceRepository invoiceRepository;

    public Invoice create(CreateInvoice invoice) {
        return invoiceRepository.save(new Invoice(invoice));
    }

    public byte[] getAsBlob(Invoice invoice) {
        return switch (invoice.getType()){
            case TICKET -> documentService.createTicketInvoice(invoice);
            case BUY -> documentService.createPurchaseInvoice(invoice);
            case SELL -> documentService.createSellInvoice(invoice);
//            case ACCESSORIES -> documentService.createPurchaseInvoice(invoice);
            default -> new byte[0];
        };
    }
}
