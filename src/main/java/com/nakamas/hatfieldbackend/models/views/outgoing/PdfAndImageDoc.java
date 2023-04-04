package com.nakamas.hatfieldbackend.models.views.outgoing;

import java.io.File;

public record PdfAndImageDoc(File image,byte[] pdfBytes) {
}
