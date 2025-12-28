package com.sep.arservice.service.impl;

import com.sep.arservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AprilTagAssetService {

    private final AprilTagPngFetcher pngFetcher;
    private final AprilTagPdfRenderer pdfRenderer;
    private final StorageService storageService;

    public Map<String, String> generateAndUpload(
            String bookId,
            String tagFamily,
            int tagId,
            double physicalWidthM,
            String markerCode
    ) {
        // 1) fetch png
        byte[] png = pngFetcher.fetchPng(tagFamily, tagId);

        // 2) render pdf
        String title = "AprilTag " + markerCode;
        byte[] pdf = pdfRenderer.renderA4(png, physicalWidthM, title);

        // 3) upload to Firebase Storage
        // object paths
        String base = String.format("markers/%s/%s/%d", bookId, tagFamily, tagId);
        String pngPath = base + "/tag.png";
        String pdfPath = base + "/tag.pdf";

        String pngUrl = storageService.save(pngPath, png, "image/png");
        String pdfUrl = storageService.save(pdfPath, pdf, "application/pdf");

        return Map.of(
                "imageUrl", pngUrl,
                "printablePdfUrl", pdfUrl
        );
    }
}

