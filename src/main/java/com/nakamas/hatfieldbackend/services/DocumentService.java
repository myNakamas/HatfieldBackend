package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.outgoing.PdfAndImageDoc;
import com.nakamas.hatfieldbackend.repositories.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.javase.QRCode;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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
public class DocumentService implements ApplicationRunner {
    private final ResourceLoader resourceLoader;
    private final InvoiceRepository invoiceRepository;
    @Value(value = "${printer-ip:#{null}}")
    private String printerIp = "";

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

    public PdfAndImageDoc createPriceTag(String qrContent, InventoryItem item) {
        InputStream input = getTemplate("/smallTag.pdf");
        try (PDDocument document = PDDocument.load(input)) {
            String deviceName = "%s %s".formatted(item.getName(), item.getBrandString());
            String model = item.getModelString();
            List<String> details = item.getOtherProperties().entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).toList();
            Float price = item.getSellPrice().floatValue();
            fillPriceTagTemplate(qrContent, deviceName, model, details, price, document);
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
    public PdfAndImageDoc createUserTag(String urlPrefix,User user) {
        InputStream input = getTemplate("/smallTag.pdf");
        String qrContent = "%s/login?username=%s&password=%s".formatted(urlPrefix, user.getUsername(),user.getFirstPassword());

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
        PDPage page = document.getPage(0);
        PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        File code = QRCode.from(qrContent).withSize(240, 240).file();
        PDImageXObject qrCode = PDImageXObject.createFromFileByContent(code, document);
        contents.drawImage(qrCode, -20, -10);
        contents.setFont(pdfFont, 25);
        contents.beginText();
        contents.newLineAtOffset(200, 180);
        contents.setLeading(35);
        contents.showText("Username: "+user.getUsername());
        contents.setFont(pdfFont, 20);
        contents.newLine();
        contents.showText("Password: "+user.getFirstPassword());
        contents.newLine();
        contents.setFont(pdfFont, 12);
        contents.setLeading(15);
        contents.showText("* You are strongly advised to change *");
        contents.newLine();
        contents.showText("* your first password once you log in! *");
        contents.endText();

        contents.close();
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
    public PdfAndImageDoc createInvoice(String qrContent, Invoice invoice) {
        InputStream input = getTemplate("/invoice.pdf");

        try (PDDocument document = PDDocument.load(input)) {
            fillInvoiceTemplate(qrContent, invoice, document);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return new PdfAndImageDoc(getImage(document, "invoice"), baos.toByteArray());
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

    private void fillPriceTagTemplate(String qrContent, String deviceName, String model, List<String> details, Float price, PDDocument document) throws IOException {
        PDFont pdfFont = PDType0Font.load(document, fontResource.getInputStream(), false);
        int detailsFontSize = details.size() > 0 ? 50 / details.size() - 5 : 25;
        int detailsLeading = details.size() > 0 ? 50 / details.size() : 30;
        PDPage page = document.getPage(0);
        PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        File code = QRCode.from(qrContent).withSize(250, 250).file();
        PDImageXObject qrCode = PDImageXObject.createFromFileByContent(code, document);
        contents.drawImage(qrCode, -20, -10);
        contents.setFont(pdfFont, 30);
        contents.beginText();
        contents.newLineAtOffset(200, 180);
        contents.setLeading(35);
        contents.showText(deviceName);
        contents.setFont(pdfFont, 30);
        contents.newLine();
        addLine(contents, model);
        contents.setFont(pdfFont, detailsFontSize);
        contents.setLeading(detailsLeading);
        for (String detail : details) {
            addLine(contents, detail);
        }
        contents.newLineAtOffset(0, -15);
        contents.setFont(pdfFont, 45);
        contents.showText("Price: £" + String.format("%.2f", price));
        contents.endText();

        contents.close();
    }

    private void fillRepairTagTemplate(String qrContent, Ticket ticket, PDDocument document) throws IOException {
        PDFont pdfFont = PDType0Font.load(document, fontResource.getInputStream(), false);
        int rows = ticket.getClient().getPhones().size() > 0 ? 5 : 6;
        int detailsFontSize = 160 / rows - 5;
        int detailsLeading = 160 / rows;
        PDPage page = document.getPage(0);
        File code = QRCode.from(qrContent).withSize(250, 250).file();
        PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        PDImageXObject qrCode = PDImageXObject.createFromFileByContent(code, document);
        contents.drawImage(qrCode, -20, -10);
        contents.setFont(pdfFont, 30);
        contents.setLeading(30);

        contents.beginText();
        contents.newLineAtOffset(200, 180);
        addLine(contents, "Ticket ID:" + ticket.getId());

        contents.setFont(pdfFont, detailsFontSize);
        contents.setLeading(detailsLeading);

        addLine(contents, ticket.getClient().getFullName());
        if (ticket.getClient().getPhones().size() > 0) {
            addLine(contents, ticket.getClient().getPhones().get(0));
        }

        addLine(contents, ticket.getAccessories());
        addLine(contents, String.format("%.2f£", ticket.getTotalPrice()));

        contents.endText();
        contents.close();
    }

    private void fillTicketTemplate(String qrContent, Ticket ticket, PDDocument document) throws IOException {
        PDFont pdfFont = PDType0Font.load(document, fontResource.getInputStream(), false);
        PDPage page = document.getPage(0);
        File code = QRCode.from(qrContent).withSize(250, 250).file();
        boolean isPaid = invoiceRepository.existsByTicketId(ticket.getId());
        PDImageXObject qrCode = PDImageXObject.createFromFileByContent(code, document);
        PDPageContentStream contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        contents.drawImage(qrCode, -20, -10, 250, 250);
        contents.setLeading(30);
        contents.setFont(pdfFont, 30);
        contents.setFont(PDType1Font.HELVETICA_BOLD, 30);

        contents.beginText();
        contents.newLineAtOffset(220, 200);
        addLine(contents, "REPAIR TICKET ID:" + ticket.getId());
        contents.setFont(PDType1Font.HELVETICA_BOLD_OBLIQUE, 30);
        contents.showText("<= Scan to track your repair");
        contents.newLine();
        contents.setFont(pdfFont, 18);
        contents.setLeading(20);
        addLine(contents, "Created at: " + ticket.getTimestamp().format(dtf));
        addLine(contents, "Brand & Model: %s ; %s".formatted(ticket.getDeviceBrandString(), ticket.getDeviceModelString()));
        addLine(contents, "Condition: " + ticket.getDeviceCondition());
        addLine(contents, "Request: " + ticket.getCustomerRequest());
        addLine(contents, String.format("Payment:  %.2f£/%s", ticket.getTotalPrice(), isPaid ? "PAID" : "NOT PAID"));
        contents.showText(String.format("Ready to collect by: %s", ticket.getDeadline().format(dtf)));
        contents.endText();

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

//        todo: add category name
        acroForm.getField("invoice_device_brand_model_name").
                setValue(invoice.getDeviceBrand() + " " + invoice.getDeviceModel());
        acroForm.getField("device_num_or_imei").setValue(invoice.getSerialNumber());
        acroForm.getField("device_count").setValue(invoice.getCount().toString());
        acroForm.getField("device_price").setValue(invoice.getTotalPrice().toString());
        acroForm.getField("invoice_note").setValue("Notes : " + invoice.getNotes());

        acroForm.getField("invoice_payment_method").setValue(invoice.getPaymentMethod().toString());
        acroForm.getField("invoice_80").setValue(String.format("%.2f",(invoice.getTotalPrice().doubleValue() / 100) * 80));
        acroForm.getField("invoice_20").setValue(String.format("%.2f",(invoice.getTotalPrice().doubleValue() / 100) * 20));
        acroForm.getField("invoice_price").setValue(invoice.getTotalPrice().toString());
        acroForm.getField("invoice_warranty").setValue(invoice.getWarrantyPeriod().toString());

        acroForm.flatten();
        contents.close();
    }

    public void executePrint(File image) {
        if (printerIp != null && !printerIp.isBlank()) {
            log.info("Printer IP provided, proceeding to print images");
            String printerUrl = "tcp://" + printerIp;
            System.setProperty("BROTHER_QL_PRINTER", printerUrl);
            System.setProperty("BROTHER_QL_MODEL", "QL-580N");
            String[] cmd = {"brother_ql", "print", "-l", "62", image.getAbsolutePath()};
            ProcessBuilder builder = new ProcessBuilder(cmd);

            try {
                Process process = builder.start();
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    log.info("Label printed successfully.");
                } else {
                    log.error("Failed to print label. Exit code: " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                log.error("Failed to print label. " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.warn("Missing Printer IP. Cannot print images");
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

    private static void addLine(PDPageContentStream contents, String ticket) throws IOException {
        contents.showText(ticket);
        contents.newLine();
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


    @Override
    public void run(ApplicationArguments args) {
//        Ticket ticket = new Ticket();
//        User user = new User();
//        user.setFullName("FullName");
//        user.setPhones(List.of("+452 2413 435 12", "+452 8513 654 12"));
//        ticket.setAccessories("One USB Cable");
//        ticket.setDeviceBrand(new Brand("Samsung"));
//        ticket.setDeviceModel(new Model("Galaxy 20"));
//        ticket.setTimestamp(ZonedDateTime.now());
//        ticket.setDeadline(ZonedDateTime.now().plusDays(5));
//        ticket.setDeviceCondition("A+");
//        ticket.setCustomerRequest("Do not reset phone");
//        ticket.setClient(user);
//        ticket.setDeposit(BigDecimal.valueOf(25.99));
//        ticket.setTotalPrice(BigDecimal.valueOf(25.99));
//        Invoice invoice = new Invoice();
//        invoice.setType(InvoiceType.SELL);
//        invoice.setDeviceModel(new Model("Galaxy 20 5G"));
//        invoice.setDeviceBrand(new Brand("SamsungS"));
//        invoice.setTimestamp(ZonedDateTime.now());
//        invoice.setNotes("blabla");
//        invoice.setSerialNumber("948376598745MASDF324");
//        invoice.setTotalPrice(BigDecimal.TEN);
//        invoice.setCreatedBy(userRepository.findUserByUsername("admin").orElse(null));
//        invoice.setPaymentMethod(PaymentMethod.CASH);
//        invoice.setWarrantyPeriod(WarrantyPeriod.ONE_MONTH);
//        if (printerIp != null && !printerIp.isBlank()) {
//            File image = createRepairTag("QR", ticket);
//            File image2 = createPriceTag("QR", "Some text", "Galaxy230", List.of("One detail", "SecondDetail"), 240f);
//            File image3 = createTicket("QR", ticket);
//            File image4 = createInvoice("QR", invoice);
//            log.info("Printer IP provided, proceeding to print images");
//            executePrint(image);
//            executePrint(image2);
//            executePrint(image3);
//            executePrint(image4);
//        } else {
//            log.warn("Missing Printer IP. Cannot print images");
//        }
    }
}