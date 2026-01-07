package com.sep.arservice.service.impl;

import com.sep.arservice.enums.AprilTagFamilySpec;
import com.sep.arservice.service.StorageService;
import com.sep.arservice.util.AprilTagImageUpscaler;
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
        if (bookId == null || bookId.isBlank()) throw new IllegalArgumentException("bookId is blank");
        if (physicalWidthM <= 0.0001) throw new IllegalArgumentException("physicalWidthM must be > 0");

        // ÉP family về tagStandard41h12
        String family = AprilTagFamilySpec.from(tagFamily).folder(); // "tagStandard41h12"

        // 1) fetch đúng nguồn tagStandard41h12/tag41_12_XXXXX.png
        byte[] pngRaw = pngFetcher.fetchPng(family, tagId);

        // 2) upscale pixel-perfect + ép nhị phân
        int targetPx = 1200;
        byte[] pngPrintable = AprilTagImageUpscaler.upscaleNearestBinary(pngRaw, targetPx);

        // 3) render PDF A4
        String title = "AprilTag " + markerCode;
        byte[] pdf = pdfRenderer.renderA4(pngPrintable, physicalWidthM, title);

        // 4) upload
        String base = String.format("markers/%s/%s/%d", bookId, family, tagId);
        String pngPath = base + "/tag.png";
        String pdfPath = base + "/tag.pdf";

        String pngUrl = storageService.save(pngPath, pngPrintable, "image/png");
        String pdfUrl = storageService.save(pdfPath, pdf, "application/pdf");

        return Map.of(
                "imageUrl", pngUrl,
                "printablePdfUrl", pdfUrl
        );
    }
}
