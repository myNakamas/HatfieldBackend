package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.outgoing.PdfAndImageDoc;
import com.nakamas.hatfieldbackend.repositories.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

@Slf4j
@Service
@Getter
public class DocumentService {
    @Value(value = "${fe-host:http://localhost:5173}")
    private String frontendHost;

    private final ResourceLoader resourceLoader;
    private final InvoiceRepository invoiceRepository;
    @Value(value = "${brother_loc:#{null}}")
    private String brotherLocation = "";
    @Value(value = "${python_loc:#{null}}")
    private String pythonLocation = "";

    private final String outputPath = Path.of(System.getProperty("user.dir"), "output").toString();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
    private final DateTimeFormatter shortDtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final DateTimeFormatter invoiceFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Resource fontResource;

    public DocumentService(ResourceLoader resourceLoader, InvoiceRepository invoiceRepository) {
        this.resourceLoader = resourceLoader;
        this.invoiceRepository = invoiceRepository;
        this.fontResource = resourceLoader.getResource("classpath:templates/fonts/arial.ttf");
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/London")
    public void removeUnneededPictures() throws IOException {
        FileUtils.cleanDirectory(new File(outputPath));
    }

    public PdfAndImageDoc createPriceTag(String qrContent, InventoryItem item) {
        InputStream input = getTemplate("/smallTag.pdf");
        try (PDDocument document = PDDocument.load(input)) {
            String deviceName = "%s".formatted(item.getName());
            List<String> details = item.getOtherProperties().entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).toList();
            Float price = item.getSellPrice() != null ? item.getSellPrice().floatValue() : 0.00f;
            fillPriceTagTemplate(qrContent, deviceName, details, price, document);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return new PdfAndImageDoc(getImage(document, "priceTag"), baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PdfAndImageDoc createRepairTag(String qrContent, Ticket ticket) {
        InputStream input = getTemplate("/smallTag.pdf");

        try (PDDocument document = PDDocument.load(input)) {
            fillRepairTagTemplate(qrContent, ticket, document);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return new PdfAndImageDoc(getImage(document, "repairTag"), baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PdfAndImageDoc createUserTag(String urlPrefix, User user) {
        InputStream input = getTemplate("/smallTag.pdf");
        String qrContent = "%s/login?username=%s&password=%s".formatted(urlPrefix, user.getUsername(), user.getFirstPassword());

        try (PDDocument document = PDDocument.load(input)) {
            fillUserTagTemplate(qrContent, user, document);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return new PdfAndImageDoc(getImage(document, "userTag"), baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillUserTagTemplate(String qrContent, User user, PDDocument document) throws IOException {
        PDFont pdfFont = PDType0Font.load(document, fontResource.getInputStream(), false);

        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        setUTF8Font(acroForm, pdfFont);

        PDPage page = document.getPage(0);
        File code = QRCode.from(qrContent).withSize(235, 235).file();
        PDImageXObject qrCode = PDImageXObject.createFromFileByContent(code, document);
        PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        contents.drawImage(qrCode, 0, -2);

        String detailsString = "You are strongly advised to change your first password once you log in!";

        acroForm.getField("title").setValue("Username: " + user.getUsername() + " Password: " + user.getFirstPassword());
        acroForm.getField("details").setValue(detailsString);
        acroForm.getField("footer").setValue(" ");

        acroForm.flatten();
        contents.close();
    }

    public PdfAndImageDoc createTicket(Ticket ticket) {
        String qr = "%s/tickets?ticketId=%s&username=%s&password=%s".formatted(frontendHost, ticket.getId(), ticket.getClient().getUsername(), ticket.getClient().getFirstPassword());
        return this.createTicket(qr, ticket);
    }

    public PdfAndImageDoc createTicket(String qrContent, Ticket ticket) {
        InputStream input = getTemplate("/ticketTag.pdf");

        try (PDDocument document = PDDocument.load(input)) {
            fillTicketTemplate(qrContent, ticket, document);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return new PdfAndImageDoc(getImage(document, "ticketTag"), baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public byte[] createInvoice(String qrContent, Invoice invoice) {
        InputStream input = getTemplate("/invoice.pdf");

        try (PDDocument document = PDDocument.load(input)) {
            fillInvoiceTemplate(qrContent, invoice, document);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getImage(PDDocument document, String name) throws IOException {
        File result = createFile(name);
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage image = renderer.renderImageWithDPI(0, 200);
        ImageIO.write(image, "png", new FileOutputStream(result));
        return result;
    }

    private void fillPriceTagTemplate(String qrContent, String deviceName, List<String> details, Float price, PDDocument document) throws IOException {
        PDFont pdfFont = PDType0Font.load(document, fontResource.getInputStream(), false);

        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        setUTF8Font(acroForm, pdfFont);

        PDPage page = document.getPage(0);
        File code = QRCode.from(qrContent).withSize(235, 235).file();
        PDImageXObject qrCode = PDImageXObject.createFromFileByContent(code, document);
        PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        contents.drawImage(qrCode, 0, -2);

        StringBuilder detailsString = new StringBuilder();
        for (String detail : details) {
            detailsString.append(detail).append("\n");
        }

        acroForm.getField("title").setValue(deviceName);
        acroForm.getField("details").setValue(detailsString.toString());
        acroForm.getField("footer").setValue("Price: £" + price.toString());

        acroForm.flatten();
        contents.close();
    }

    private void fillRepairTagTemplate(String qrContent, Ticket ticket, PDDocument document) throws IOException {
        PDFont pdfFont = PDType0Font.load(document, fontResource.getInputStream(), false);
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        setUTF8Font(acroForm, pdfFont);

        PDPage page = document.getPage(0);
        File code = QRCode.from(qrContent).withSize(235, 235).file();
        PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        PDImageXObject qrCode = PDImageXObject.createFromFileByContent(code, document);
        contents.drawImage(qrCode, 0, -2);

        StringBuilder details = new StringBuilder(ticket.getClient().getFullName() + "\n");
        List<String> phones = ticket.getClient().getPhones();
        for (int i = 0; i < phones.size(); i++) {
            String phone = phones.get(i);
            details.append("Phone #").append(i).append(" ").append(phone).append("\n");
        }

        acroForm.getField("title").setValue("Ticket ID:" + ticket.getId());
        acroForm.getField("details").setValue(details.toString());
        acroForm.getField("footer").setValue("Price: £" + ticket.getTotalPrice());

        acroForm.flatten();
        contents.close();
    }

    private void fillTicketTemplate(String qrContent, Ticket ticket, PDDocument document) throws IOException {
        PDFont pdfFont = PDType0Font.load(document, fontResource.getInputStream(), false);
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        setUTF8Font(acroForm, pdfFont);

        PDPage page = document.getPage(0);
        File code = QRCode.from(qrContent).withSize(230, 230).file();
        PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        PDImageXObject qrCode = PDImageXObject.createFromFileByContent(code, document);
        contents.drawImage(qrCode, 0, -2);

        boolean isPaid = invoiceRepository.existsByTicketId(ticket.getId());
        String details = "Created at: " + ticket.getTimestamp().format(dtf) + "\n" +
                "Brand & Model: " + ticket.getDeviceBrandString() + " ; " + ticket.getDeviceModelString() + "\n" +
                "Condition: " + ticket.getDeviceCondition() + "\n" +
                "Request: " + ticket.getCustomerRequest() + "\n" +
                "Payment:  " + ticket.getTotalPrice() + (isPaid ? "/PAID" : "/NOT PAID") + "\n" +
                "Ready to collect by: " + ticket.getDeadline().format(dtf) + "\n";


        acroForm.getField("ticket_id").setValue("REPAIR TICKET ID:" + ticket.getId());
        acroForm.getField("details").setValue(details);

        acroForm.flatten();
        contents.close();
    }

    private void fillInvoiceTemplate(String qrContent, Invoice invoice, PDDocument document) throws IOException {
        PDFont pdfFont = PDType0Font.load(document, fontResource.getInputStream(), false);

        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        setUTF8Font(acroForm, pdfFont);

        PDPage page = document.getPage(0);
        File code = QRCode.from(qrContent).withSize(170, 170).file();
        PDImageXObject qrCode = PDImageXObject.createFromFileByContent(code, document);
        PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        contents.drawImage(qrCode, 360, 470, 170, 170);
        contents.setLeading(6);
        contents.setFont(pdfFont, 11);
        contents.setFont(pdfFont, 11);

        String id = String.format("%019d", invoice.getId());
        acroForm.getField("invoice_id").setValue(id);
        acroForm.getField("invoice_date_time").setValue(invoiceFormatter.format(invoice.getTimestamp()));
        acroForm.getField("invoice_type").setValue(invoice.getType().toString());
        acroForm.getField("invoice_creator_name").setValue(invoice.getCreatedBy().getFullName());

        acroForm.getField("shop_phone").setValue(invoice.getCreatedBy().getShop().getPhone());
        acroForm.getField("shop_vat").setValue(invoice.getCreatedBy().getShop().getVatNumber());
        acroForm.getField("shop_reg").setValue(invoice.getCreatedBy().getShop().getRegNumber());
        acroForm.getField("shop_locations").setValue(invoice.getCreatedBy().getShop().getAddress());

        acroForm.getField("invoice_device_brand_model_name").setValue(invoice.getDeviceName());
        acroForm.getField("device_num_or_imei").setValue(invoice.getSerialNumber());
        acroForm.getField("device_count").setValue(invoice.getCount().toString());
        acroForm.getField("device_price").setValue(invoice.getTotalPrice().toString());
        acroForm.getField("invoice_note").setValue("Notes : " + (invoice.getNotes() != null ? invoice.getNotes() : "None"));

        acroForm.getField("invoice_payment_method").setValue(invoice.getPaymentMethod().toString());
        acroForm.getField("invoice_80").setValue(String.format("%.2f", (invoice.getTotalPrice().doubleValue() / 100) * 80));
        acroForm.getField("invoice_20").setValue(String.format("%.2f", (invoice.getTotalPrice().doubleValue() / 100) * 20));
        acroForm.getField("invoice_price").setValue(invoice.getTotalPrice().toString());
        acroForm.getField("invoice_warranty").setValue(invoice.getWarrantyPeriod().toString());

        acroForm.flatten();
        contents.close();
    }

    public void executePrint(File image) throws CustomException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.error("No authenticated user found, skipping print");
            return;
        }
        ShopSettings settings = ((User) authentication.getPrincipal()).getShop().getSettings();
        if (!settings.isPrintEnabled()) {
            log.info("Shop settings do not allow printing. Cannot print images.");
            return;
        }
        if (settings.getPrinterIp() == null || settings.getPrinterIp().isBlank() || brotherLocation.isBlank() || settings.getPrinterModel() == null || settings.getPrinterModel().isBlank()) {
            throw new CustomException("Missing Printer IP, Model or library location. Cannot print images.");
        }
        log.info("Printer IP provided, proceeding to print images");
        String printerUrl = "tcp://" + settings.getPrinterIp();
        List<String> cmd = List.of(brotherLocation, "-b", "network", "-p", printerUrl, "-m", settings.getPrinterModel(), "print", "-l", "62", image.getAbsolutePath());
        log.info("Running " + String.join(" ", cmd));
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.environment().put("BROTHER_QL_PRINTER", printerUrl);
        builder.environment().put("BROTHER_QL_MODEL", settings.getPrinterModel());
        if (pythonLocation != null && !pythonLocation.isBlank()) builder.environment().put("PYTHONPATH", pythonLocation);
        builder.inheritIO();
        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Label printed successfully.");
            } else {
                throw new CustomException("Failed to print label. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new CustomException("Failed to print label. " + e.getMessage());
        }
    }

    private File createFile(String name) throws IOException {
        String filename = name + ZonedDateTime.now().format(shortDtf) + ".png";
        String filePath = outputPath + "/" + filename;
        Files.createDirectories(Path.of(outputPath));
        File file = new File(filePath);
        log.info("Created image to [%s]".formatted(file.getAbsolutePath()));
        return file;
    }

    private InputStream getTemplate(String location) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates" + location);
            return resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setUTF8Font(PDAcroForm acroForm, PDFont font) {
        PDResources defaultResources = acroForm.getDefaultResources();
        COSName fontName = defaultResources.add(font);
        acroForm.setDefaultResources(defaultResources);
        for (PDField field : acroForm.getFields()) {
            if (field instanceof PDTextField textField) {
                String defaultAppearance = textField.getDefaultAppearance();
                defaultAppearance = defaultAppearance.replaceFirst("/Helv", "/" + fontName.getName());
                textField.setDefaultAppearance(defaultAppearance);
            }
        }
    }

    /**
     * Used to test the printing
     */
    public void executeExamplePrint() {
        InputStream input = getTemplate("/smallTag.pdf");
        try (PDDocument document = PDDocument.load(input)) {
            String deviceName = "Testing ticket";
            List<String> details = List.of();
            Float price = 0.00f;
            fillPriceTagTemplate("Test", deviceName, details, price, document);
            executePrint(getImage(document, "priceTag"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}