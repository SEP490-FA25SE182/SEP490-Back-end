package com.sep.arservice.service.impl;

import com.sep.arservice.dto.CreateAprilTagMarkerRequest;
import com.sep.arservice.dto.MarkerRequest;
import com.sep.arservice.dto.MarkerResponse;
import com.sep.arservice.enums.AprilTagFamilySpec;
import com.sep.arservice.enums.IsActived;
import com.sep.arservice.mapper.MarkerMapper;
import com.sep.arservice.model.Marker;
import com.sep.arservice.model.PageMarker;
import com.sep.arservice.repository.MarkerRepository;
import com.sep.arservice.repository.PageMarkerRepository;
import com.sep.arservice.service.MarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkerServiceImpl implements MarkerService {
    private final MarkerRepository repo;
    private final MarkerMapper mapper;
    private final PageMarkerRepository pageMarkerRepo;

    private final AprilTagAssetService aprilTagAssetService;

    private static final String MARKER_TYPE_APRILTAG = "APRILTAG";
    private static final String DEFAULT_FAMILY = "tagStandard41h12";

    @Override @Transactional(readOnly = true)
    public List<MarkerResponse> getAll() {
        return repo.findAllByIsActived(IsActived.ACTIVE)
                .stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public MarkerResponse getById(String id) {
        Marker e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Marker not found: " + id));
        if (e.getIsActived() != IsActived.ACTIVE) {
            throw new RuntimeException("Marker is inactive: " + id);
        }
        return mapper.toResponse(e);
    }

    @Override
    public MarkerResponse create(MarkerRequest req) {
        if (repo.existsByMarkerCodeIgnoreCaseAndIsActived(req.getMarkerCode(), IsActived.ACTIVE)) {
            throw new IllegalArgumentException("markerCode duplicated");
        }
        Marker e = new Marker();
        mapper.copyForCreate(req, e);
        e.setIsActived(IsActived.ACTIVE);
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public MarkerResponse update(String id, MarkerRequest req) {
        Marker e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Marker not found: " + id));
        if (e.getIsActived() != IsActived.ACTIVE) {
            throw new RuntimeException("Marker is inactive: " + id);
        }
        mapper.copyForUpdate(req, e);
        e.setUpdatedAt(Instant.now());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public void softDelete(String id) {
        Marker e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Marker not found: " + id));
        e.setIsActived(IsActived.INACTIVE);
        e.setUpdatedAt(Instant.now());
        repo.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MarkerResponse> search(String markerCode, String markerType, String bookId, String pageId, String userId, Pageable pageable) {

        Marker probe = new Marker();
        if (markerCode != null && !markerCode.isBlank()) {
            probe.setMarkerCode(markerCode.trim());
        }
        if (markerType != null && !markerType.isBlank()) {
            probe.setMarkerType(markerType.trim());
        }
        if (bookId != null && !bookId.isBlank()) {
            probe.setBookId(bookId.trim());
        }
        if (userId != null && !userId.isBlank()) {
            probe.setUserId(userId.trim());
        }
        probe.setIsActived(IsActived.ACTIVE);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("markerCode", mm -> mm.ignoreCase().contains())
                .withMatcher("markerType", mm -> mm.ignoreCase())
                .withMatcher("bookId",     mm -> mm.ignoreCase())
                .withMatcher("userId",     mm -> mm.ignoreCase())
                .withIgnoreNullValues()
                .withIgnorePaths("printablePdfUrl", "physicalWidthM", "createdAt", "updatedAt");

        Example<Marker> example = Example.of(probe, matcher);

        // 1) Không truyền pageId
        if (pageId == null || pageId.isBlank()) {
            return repo.findAll(example, pageable).map(mapper::toResponse);
        }

        // 2) Có pageId
        List<PageMarker> links = pageMarkerRepo.findAllByPageId(pageId);
        if (links.isEmpty()) {
            return Page.empty(pageable);
        }

        Set<String> markerIds = links.stream()
                .map(PageMarker::getMarkerId)
                .collect(Collectors.toSet());

        Page<Marker> page = repo.findByMarkerIdInAndIsActived(markerIds, IsActived.ACTIVE, pageable);

        Stream<Marker> stream = page.getContent().stream();

        if (markerCode != null && !markerCode.isBlank()) {
            String mc = markerCode.trim().toLowerCase();
            stream = stream.filter(m -> m.getMarkerCode() != null && m.getMarkerCode().toLowerCase().contains(mc));
        }

        if (markerType != null && !markerType.isBlank()) {
            String mt = markerType.trim().toLowerCase();
            stream = stream.filter(m -> m.getMarkerType() != null && m.getMarkerType().toLowerCase().equals(mt));
        }

        if (bookId != null && !bookId.isBlank()) {
            String bid = bookId.trim().toLowerCase();
            stream = stream.filter(m -> m.getBookId() != null && m.getBookId().toLowerCase().equals(bid));
        }

        List<MarkerResponse> content = stream.map(mapper::toResponse).toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }


    @Override
    public MarkerResponse createWithPage(String pageId, MarkerRequest req) {
        // 1. Tạo marker như bình thường
        Marker e = new Marker();
        mapper.copyForCreate(req, e); // giả sử mapper đã có
        e.setMarkerId(null); // để JPA tự generate
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        e.setIsActived(IsActived.ACTIVE);

        Marker saved = repo.save(e);

        // 2. Tạo bản ghi PageMarker để link marker với page
        PageMarker pm = new PageMarker();
        pm.setPageId(pageId);
        pm.setMarkerId(saved.getMarkerId());
        pageMarkerRepo.save(pm);

        return mapper.toResponse(saved);
    }

    @Override
    public MarkerResponse attachPage(String markerId, String pageId) {
        // 1. Tìm marker
        Marker marker = repo.findById(markerId)
                .orElseThrow(() -> new RuntimeException("Marker not found: " + markerId));

        if (marker.getIsActived() != IsActived.ACTIVE) {
            throw new RuntimeException("Marker is inactive: " + markerId);
        }

        // (Optional) Bạn có PageRepository thì có thể verify page tồn tại + active ở đây

        // 2. Check đã link chưa để tránh trùng
        boolean existed = pageMarkerRepo.existsByPageIdAndMarkerId(pageId, markerId);
        if (existed) {
            // có thể return luôn, hoặc ném exception tuỳ bạn
            return mapper.toResponse(marker);
        }

        // 3. Tạo link PageMarker
        PageMarker link = new PageMarker();
        link.setPageId(pageId);
        link.setMarkerId(markerId);
        pageMarkerRepo.save(link);

        // 4. Trả về marker
        return mapper.toResponse(marker);
    }

    @Override
    public MarkerResponse createAprilTag(CreateAprilTagMarkerRequest req) {
        String family = AprilTagFamilySpec.from(
                (req.getTagFamily() == null || req.getTagFamily().isBlank()) ? DEFAULT_FAMILY : req.getTagFamily()
        ).folder();

        double sizeM = (req.getPhysicalWidthM() != null && req.getPhysicalWidthM() > 0)
                ? req.getPhysicalWidthM()
                : 0.10d;

        AprilTagFamilySpec spec = AprilTagFamilySpec.from(family);

        for (int attempt = 0; attempt < 3; attempt++) {

            int nextTagId = repo
                    .findTopByBookIdAndTagFamilyAndIsActivedOrderByTagIdDesc(req.getBookId(), family, IsActived.ACTIVE)
                    .map(m -> (m.getTagId() == null ? 0 : m.getTagId() + 1))
                    .orElse(0);

            if (nextTagId > spec.maxId()) {
                throw new IllegalStateException("Book exceeded max tags for " + family +
                        " (next=" + nextTagId + ", max=" + spec.maxId() + ")");
            }

            Marker e = new Marker();
            e.setBookId(req.getBookId());
            e.setTagFamily(family);
            e.setTagId(nextTagId);
            e.setMarkerType(MARKER_TYPE_APRILTAG);

            String markerCode = (req.getMarkerCode() != null && !req.getMarkerCode().isBlank())
                    ? req.getMarkerCode().trim()
                    : buildMarkerCode(req.getBookId(), family, nextTagId);

            e.setMarkerCode(markerCode);
            e.setPhysicalWidthM(sizeM);
            e.setUserId(req.getUserId());
            e.setIsActived(IsActived.ACTIVE);
            e.setCreatedAt(Instant.now());
            e.setUpdatedAt(Instant.now());

            try {
                Marker saved = repo.save(e);

                var urls = aprilTagAssetService.generateAndUpload(
                        saved.getBookId(),
                        saved.getTagFamily(),
                        saved.getTagId(),
                        saved.getPhysicalWidthM(),
                        saved.getMarkerCode()
                );

                saved.setImageUrl(urls.get("imageUrl"));
                saved.setPrintablePdfUrl(urls.get("printablePdfUrl"));
                saved.setUpdatedAt(Instant.now());

                return mapper.toResponse(repo.save(saved));

            } catch (DataIntegrityViolationException ex) {
                if (attempt == 2) throw ex;
            }
        }

        throw new IllegalStateException("Cannot allocate AprilTag id after retries");
    }

    private String buildMarkerCode(String bookId, String family, int tagId) {
        return bookId + ":" + family + ":" + tagId;
    }
}
