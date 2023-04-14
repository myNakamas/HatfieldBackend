package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InvoiceFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PdfAndImageDoc;
import com.nakamas.hatfieldbackend.models.views.outgoing.reports.InvoiceDailyReport;
import com.nakamas.hatfieldbackend.models.views.outgoing.reports.InvoiceReport;
import com.nakamas.hatfieldbackend.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InvoicingService {
    private final InvoiceRepository invoiceRepository;
    private final UserService userService;
    private final DocumentService documentService;

    public Invoice create(CreateInvoice invoice) {
        return invoiceRepository.save(new Invoice(invoice,
                userService.getUser(invoice.getCreatedBy()),
                invoice.getClient() == null ? null : userService.getUser(invoice.getClient())));
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

    public InvoiceReport getMonthlyReport(InvoiceFilter invoiceFilter) {
        List<Invoice> all = invoiceRepository.findAll(invoiceFilter);
        Map<LocalDate, InvoiceDailyReport> dailyReportsByDate = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Invoice invoice : all) {
            LocalDate date = invoice.getTimestamp().toLocalDate();

            InvoiceDailyReport dailyReport = dailyReportsByDate.computeIfAbsent(date,
                    key -> new InvoiceDailyReport(date, BigDecimal.ZERO, 0));

            BigDecimal dailyIncome = dailyReport.dailyIncome().add(invoice.getTotalPrice());
            int dailyCount = dailyReport.count() + 1;

            dailyReport = new InvoiceDailyReport(date, dailyIncome, dailyCount);
            dailyReportsByDate.put(date, dailyReport);
            totalAmount = totalAmount.add(invoice.getTotalPrice());
        }

        List<InvoiceDailyReport> dailyReports = new ArrayList<>(dailyReportsByDate.values());
        return new InvoiceReport(all.size(), totalAmount, dailyReports);
    }
}