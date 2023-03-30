package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final DocumentService documentService;

    public Invoice create(CreateInvoice invoice) {
        return invoiceRepository.save(new Invoice(invoice));
    }

    public Invoice getById(Long invoiceId){return invoiceRepository.findById(invoiceId).orElse(null);}

    public List<Invoice> getByClientId(UUID clientId){return invoiceRepository.findAllByClient_Id(clientId);}

    public List<Invoice> getAll(){return invoiceRepository.findAll();}

    public byte[] getAsBlob(Invoice invoice){
        //        todo: edit the qr information just like in documentController
        File image = documentService.createInvoice("QR", invoice);
        documentService.executePrint(image);
        try{
            return Files.readAllBytes(image.toPath());
        }catch (IOException e){
            throw new CustomException(e.getMessage());
        }
    }
}
