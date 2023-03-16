package com.nakamas.hatfieldbackend.services;

import lombok.RequiredArgsConstructor;
import net.glxn.qrgen.javase.QRCode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
public class DocumentService implements ApplicationRunner {
    private final static String RESOURCES_LOCATION = "src/main/resources";
    private final static String PDF_TEMPLATE_LOCATION = RESOURCES_LOCATION + "/templates";

    public void fillDocument() {
        InputStream input = null;
        try {
            input = new FileInputStream(PDF_TEMPLATE_LOCATION + "/new_document.pdf");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        //Load editable pdf file
        try (PDDocument pdfDoc = PDDocument.load(input)) {
            PDPage page = pdfDoc.getPage(0);

            page.setCropBox(PDRectangle.A6);
            page.setTrimBox(PDRectangle.A6);
            File file = QRCode.from("Hello World").withSize(250, 250).file();

            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(file, pdfDoc);
            PDPageContentStream contents = new PDPageContentStream(pdfDoc, page, PDPageContentStream.AppendMode.APPEND, true, true);
            contents.drawImage(pdImage, 5, 5);

            PDDocumentCatalog docCatalog = pdfDoc.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();

            PDField firstnameField = acroForm.getField("username");
            firstnameField.setValue("CUSTOM USERNAME");

            PDField lastnameField = acroForm.getField("password");
            lastnameField.setValue("passwordu");

            /*make the final document uneditable*/
            acroForm.flatten();
            /*generate a new pdf file and save it to the given location*/
            contents.close();
            pdfDoc.save(new File(PDF_TEMPLATE_LOCATION + "/new_document2.pdf"));
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void run(ApplicationArguments args) {
        fillDocument();
    }
}