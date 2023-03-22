package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice create(CreateInvoice invoice){
        return invoiceRepository.save(new Invoice(invoice));
    }

}
