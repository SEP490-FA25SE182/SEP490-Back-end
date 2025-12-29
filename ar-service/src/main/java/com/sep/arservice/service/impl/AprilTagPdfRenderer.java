package com.sep.arservice.service.impl;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class AprilTagPdfRenderer {

    // 1 inch = 72pt; 1m = 39.37007874 inch
    private static float metersToPoints(double meters) {
        return (float) (meters * 39.37007874d * 72d);
    }

    public byte[] renderA4(byte[] pngBytes, double physicalWidthM, String titleLine) {
        if (physicalWidthM <= 0.0001) throw new IllegalArgumentException("physicalWidthM must be > 0");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDImageXObject img = PDImageXObject.createFromByteArray(doc, pngBytes, "apriltag.png");

            float pageW = page.getMediaBox().getWidth();
            float pageH = page.getMediaBox().getHeight();

            // desired printed width in points
            float targetW = metersToPoints(physicalWidthM);
            float targetH = targetW; // tag is square

            // center on page, keep some margin
            float x = (pageW - targetW) / 2f;
            float y = (pageH - targetH) / 2f;

            // move slightly up to leave room for caption
            y += 40f;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.drawImage(img, x, y, targetW, targetH);

                // Caption
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, 40);
                cs.showText(titleLine);
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 10);
                cs.newLineAtOffset(50, 25);
                cs.showText("Print size (tag edge): " + String.format("%.3f m", physicalWidthM));
                cs.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Render AprilTag PDF failed", e);
        }
    }
}

