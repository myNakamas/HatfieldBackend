package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.enums.InvoiceType;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InvoiceFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.reports.InvoiceDailyReport;
import com.nakamas.hatfieldbackend.models.views.outgoing.reports.InvoiceReport;
import com.nakamas.hatfieldbackend.models.views.outgoing.reports.SellReport;
import com.nakamas.hatfieldbackend.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InvoicingService {
    @Value(value = "${fe-host:localhost:5173}")
    private String frontendHost;
    private final InvoiceRepository invoiceRepository;
    private final UserService userService;
    private final InventoryItemService inventoryItemService;
    private final DocumentService documentService;
    private final LoggerService loggerService;

    public Invoice create(CreateInvoice invoice, User user) {
        if (invoice.getItemId() != null) updateItemFromInvoice(invoice);
        Invoice newInvoice = invoiceRepository.save(new Invoice(invoice,
                user,
                invoice.getClientId() == null ? null : userService.getUser(invoice.getClientId())));
        loggerService.createLog(new Log(LogType.getLogType(invoice.getType()), newInvoice.getId()), newInvoice.getId());
        return newInvoice;
    }

    public Invoice getByTicketId(Long id) {
        List<Invoice> invoices = invoiceRepository.findByTicketId(id);
        Optional<Invoice> first = invoices.stream().filter(Invoice::isValid).findFirst();
        return first.orElse(null);
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
        String qrContent = "%s/invoices?invoiceId=%s".formatted(frontendHost, invoice.getId());
        return documentService.createInvoice(qrContent, invoice);
    }

    public InvoiceReport getInvoiceMonthlyReport(InvoiceFilter invoiceFilter) {
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

    public SellReport getSellReport(InvoiceFilter invoiceFilter) {
        invoiceFilter.setType(InvoiceType.SELL);
        List<Invoice> all = invoiceRepository.findAll(invoiceFilter);
        Map<String, Integer> sellingReport = new HashMap<>();
        BigDecimal amount = BigDecimal.ZERO;
        for (Invoice invoice : all) {
            String itemName = invoice.getDeviceName();

            sellingReport.putIfAbsent(itemName, 0);
            sellingReport.compute(itemName, (s, count) -> count == null ? 1 : count + 1);
            amount = amount.add(invoice.getTotalPrice());
        }

        return new SellReport(amount, sellingReport);
    }

    public void invalidateInvoice(Long invoiceId) {
        Invoice byId = getById(invoiceId);
        byId.setValid(false);
        loggerService.createLog(new Log(LogType.INVALIDATED_INVOICE, invoiceId), invoiceId);
        invoiceRepository.save(byId);
    }

    public Invoice getDepositInvoice(Long ticketId) {
        List<Invoice> invoices = invoiceRepository.findByTicketIdAndType(ticketId, InvoiceType.DEPOSIT);
        Optional<Invoice> first = invoices.stream().filter(Invoice::isValid).findFirst();
        if (first.isEmpty()) throw new CustomException("No deposit invoice was found for this ticket");
        return first.get();
    }

    private void updateItemFromInvoice(CreateInvoice invoice) {
        switch (invoice.getType()){
            case BUY -> inventoryItemService.buyItem(invoice.getItemId(),invoice.getCount());
            case SELL,ACCESSORIES -> inventoryItemService.sellItem(invoice.getItemId(), invoice.getCount());
            default -> {}
        }

    }
}
