package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InvoiceFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PdfAndImageDoc;
import com.nakamas.hatfieldbackend.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoicingService {
    private final InvoiceRepository invoiceRepository;
    private final UserService userService;
    private final DocumentService documentService;

    public Invoice create(CreateInvoice invoice) {
        return invoiceRepository.save(new Invoice(invoice,
                userService.getUser(invoice.getCreatedBy()),
                userService.getUser(invoice.getClient())));
    }

    public Invoice getByTicketId(Long id) {
        List<Invoice> invoices = invoiceRepository.findByTicketId(id);
        if (invoices.size() > 0) return invoices.get(0);
        return null;
    }

    public Invoice getById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId).orElse(null);
    }

    public List<Invoice> getByClientId(UUID clientId) {
        return invoiceRepository.findAllByClient_Id(clientId);
    }

    public Page<Invoice> getAll(InvoiceFilter invoiceFilter, PageRequestView pageRequestView) {
        return invoiceRepository.findAll(invoiceFilter, pageRequestView.getPageRequest());
    }

    public byte[] getAsBlob(Invoice invoice) {
        //        todo: edit the qr information just like in documentController
        PdfAndImageDoc doc = documentService.createInvoice("QR", invoice);
        documentService.executePrint(doc.image());
        return doc.pdfBytes();
    }
}
