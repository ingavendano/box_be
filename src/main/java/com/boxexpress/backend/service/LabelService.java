package com.boxexpress.backend.service;

import com.boxexpress.backend.model.Package;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class LabelService {

    // 6x4 inches in points (Landscape)
    // Width: 6 * 72 = 432
    // Height: 4 * 72 = 288
    private static final Rectangle PAGE_SIZE = new Rectangle(432, 288);

    public byte[] generateLabel(Package pkg) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PAGE_SIZE, 10, 10, 10, 10);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            PdfContentByte cb = writer.getDirectContent();

            // --- Fonts ---
            BaseFont bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            BaseFont bfNormal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

            // --- Border (Rounded) ---
            cb.setLineWidth(1.5f);
            cb.roundRectangle(15, 15, 402, 258, 10); // x, y, w, h, mw
            cb.stroke();

            // --- Header ---
            // Logo Text
            cb.beginText();
            cb.setFontAndSize(bfBold, 14);
            cb.setTextMatrix(30, 258);
            cb.showText("BOX EXPRESS");
            cb.endText();

            cb.beginText();
            cb.setFontAndSize(bfNormal, 10);
            cb.setTextMatrix(30, 245);
            cb.showText("    de El Salvador");
            cb.endText();

            // Tracking Label
            cb.beginText();
            cb.setFontAndSize(bfBold, 12);
            cb.setTextMatrix(200, 248);
            cb.showText("TRACKING NO:: " + pkg.getTrackingId());
            cb.endText();

            // Divider 1
            drawLine(cb, 235);

            // --- SENDER ---
            cb.beginText();
            cb.setFontAndSize(bfBold, 10);
            cb.setTextMatrix(30, 215);
            cb.showText("SENDER:");
            cb.endText();

            cb.beginText();
            cb.setFontAndSize(bfNormal, 10);
            cb.setTextMatrix(85, 215);
            cb.showText(pkg.getSenderName() + " / " + pkg.getOriginCity());
            cb.setTextMatrix(85, 203);
            cb.showText("Tel: " + (pkg.getReceiverPhone() != null ? "N/A" : "N/A"));
            cb.endText();

            // Divider 2
            drawLine(cb, 190);

            // --- RECIPIENT ---
            cb.beginText();
            cb.setFontAndSize(bfBold, 10);
            cb.setTextMatrix(30, 175);
            cb.showText("RECIPIENT:");
            cb.endText();

            cb.beginText();
            cb.setFontAndSize(bfBold, 16);
            cb.setTextMatrix(30, 155);
            cb.showText(pkg.getReceiverName());
            cb.endText();

            cb.beginText();
            cb.setFontAndSize(bfNormal, 11);
            cb.setTextMatrix(30, 140);
            cb.showText(pkg.getDestinationAddress());
            cb.setTextMatrix(30, 125);
            cb.showText(pkg.getDestinationCity() + ", EL SALVADOR");
            cb.setTextMatrix(30, 110);
            cb.showText("Ref: " + pkg.getDescription());
            cb.endText();

            // Divider 3
            drawLine(cb, 95);

            // --- BARCODE (Linear) ---
            Barcode128 code128 = new Barcode128();
            code128.setCode(pkg.getTrackingId());
            code128.setCodeType(Barcode128.CODE128);
            code128.setBarHeight(40f);
            code128.setX(1.1f); // Bar width slightly adjusted for space
            code128.setFont(null);

            Image codeImage = code128.createImageWithBarcode(cb, null, null);
            codeImage.setAbsolutePosition(30, 35);
            document.add(codeImage);

            // Tracking Text below barcode
            cb.beginText();
            cb.setFontAndSize(bfNormal, 9);
            cb.setTextMatrix(50, 25);
            cb.showText(pkg.getTrackingId());
            cb.endText();

            // --- SERVICE TYPE ---
            cb.beginText();
            cb.setFontAndSize(bfBold, 14);
            cb.setTextMatrix(260, 45); // Right side
            cb.showText("SERVICE: EXPRESS");
            cb.endText();

            // --- Footer ---
            String dateStr = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            cb.beginText();
            cb.setFontAndSize(bfNormal, 8);
            cb.setTextMatrix(260, 30);
            cb.showText("Printed: " + dateStr);
            cb.endText();

            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating label PDF", e);
        }
    }

    private void drawLine(PdfContentByte cb, float y) {
        cb.moveTo(15, y);
        cb.lineTo(417, y);
        cb.setLineWidth(1f);
        cb.stroke();
    }
}
