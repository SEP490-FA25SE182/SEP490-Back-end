package com.sep.arservice.service.impl;

import com.sep.arservice.dto.CreateMarkerIllustrationRequest;
import com.sep.arservice.dto.MarkerResponse;
import com.sep.arservice.enums.IsActived;
import com.sep.arservice.model.Marker;
import com.sep.arservice.repository.MarkerRepository;
import com.sep.arservice.service.StorageService;
import com.sep.arservice.util.AprilTagEmbedder;
import com.sep.arservice.util.HttpBytesFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkerIllustrationService {

    private static final String MARKER_TYPE_ILLU = "markerIllustration";
    private static final String TAG_FAMILY = "tagStandard41h12";
    private static final double TAG_SIZE_METERS = 0.03;

    private final MarkerRepository markerRepo;
    private final HttpBytesFetcher httpBytesFetcher;
    private final StorageService storageService;

    @Transactional
    public MarkerResponse createMarkerIllustration(CreateMarkerIllustrationRequest req) {
        if (req == null || !StringUtils.hasText(req.getMarkerId())) {
            throw new IllegalArgumentException("markerId is blank");
        }
        if (!StringUtils.hasText(req.getIllustrationImageUrl())) {
            throw new IllegalArgumentException("illustrationImageUrl is blank");
        }

        // 0) Load base marker
        Marker baseMarker = markerRepo.findById(req.getMarkerId())
                .orElseThrow(() -> new IllegalArgumentException("Marker not found: " + req.getMarkerId()));

        if (!StringUtils.hasText(baseMarker.getImageUrl())) {
            throw new IllegalStateException("Base marker imageUrl is empty (tag png url). markerId=" + baseMarker.getMarkerId());
        }
        if (!StringUtils.hasText(baseMarker.getBookId())) {
            throw new IllegalStateException("Base marker bookId is empty. markerId=" + baseMarker.getMarkerId());
        }
        if (!StringUtils.hasText(baseMarker.getMarkerCode())) {
            throw new IllegalStateException("Base marker markerCode is empty. markerId=" + baseMarker.getMarkerId());
        }

        // 1) Download bytes
        byte[] illuBytes = httpBytesFetcher.get(req.getIllustrationImageUrl());
        byte[] tagBytes = httpBytesFetcher.get(baseMarker.getImageUrl());

        // 2) Embed tag top-right
        byte[] outPng = AprilTagEmbedder.embedTopRight(
                illuBytes,
                tagBytes,
                TAG_SIZE_METERS,
                req.getCamoStrength(),
                req.getQuietZoneAlpha(),
                req.getAssumedDpi(),
                req.getGrainStrength()
        );


        // 3) Allocate nextTagId + create marker
        String bookId = baseMarker.getBookId();

        for (int attempt = 0; attempt < 3; attempt++) {
            int nextTagId = markerRepo
                    .findTopByBookIdAndTagFamilyAndIsActivedOrderByTagIdDesc(bookId, TAG_FAMILY, IsActived.ACTIVE)
                    .map(m -> (m.getTagId() == null ? 0 : m.getTagId() + 1))
                    .orElse(0);

            // 3.1) Upload to Firebase using NEXT tagId
            String objectPath = String.format("marker_illustration/%s/%d.png", bookId, nextTagId);

            String newImageUrl = storageService.save(objectPath, outPng, "image/png");

            // 3.2) Build markerCode = MI + oldCode
            String code = "Mi_" + baseMarker.getMarkerCode();
            if (markerRepo.existsByMarkerCodeIgnoreCaseAndIsActived(code, IsActived.ACTIVE)) {
                code = code + "_" + shortId();
            }

            Marker m = Marker.builder()
                    .markerId(null)
                    .imageUrl(newImageUrl)
                    .markerCode(code)
                    .markerType(MARKER_TYPE_ILLU)
                    .physicalWidthM(baseMarker.getPhysicalWidthM())
                    .printablePdfUrl(null)
                    .userId(baseMarker.getUserId())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .isActived(IsActived.ACTIVE)
                    .bookId(bookId)
                    .tagFamily(TAG_FAMILY)
                    .tagId(nextTagId)
                    .build();

            try {
                Marker saved = markerRepo.save(m);
                return toResponse(saved);
            } catch (DataIntegrityViolationException ex) {
                if (attempt == 2) throw ex;
            }
        }

        throw new IllegalStateException("Cannot allocate markerIllustration tagId after retries");
    }

    private static String shortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private static MarkerResponse toResponse(Marker m) {
        return MarkerResponse.builder()
                .markerId(m.getMarkerId())
                .markerCode(m.getMarkerCode())
                .markerType(m.getMarkerType())
                .imageUrl(m.getImageUrl())
                .printablePdfUrl(m.getPrintablePdfUrl())
                .bookId(m.getBookId())
                .tagFamily(m.getTagFamily())
                .tagId(m.getTagId())
                .physicalWidthM(m.getPhysicalWidthM())
                .build();
    }
}