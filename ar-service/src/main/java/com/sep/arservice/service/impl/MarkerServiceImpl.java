package com.sep.arservice.service.impl;

import com.sep.arservice.dto.MarkerRequest;
import com.sep.arservice.dto.MarkerResponse;
import com.sep.arservice.enums.IsActived;
import com.sep.arservice.mapper.MarkerMapper;
import com.sep.arservice.model.Marker;
import com.sep.arservice.model.PageMarker;
import com.sep.arservice.repository.MarkerRepository;
import com.sep.arservice.repository.PageMarkerRepository;
import com.sep.arservice.service.MarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
    public Page<MarkerResponse> search(String markerCode, String markerType, String pageId, String userId, Pageable pageable) {
        // ==== Build Example cho trường hợp KHÔNG có pageId ====
        Marker probe = new Marker();
        if (markerCode != null && !markerCode.isBlank()) {
            probe.setMarkerCode(markerCode.trim());
        }
        if (markerType != null && !markerType.isBlank()) {
            probe.setMarkerType(markerType.trim());
        }
        if (userId != null && !userId.isBlank()) {
            probe.setUserId(userId.trim());
        }
        probe.setIsActived(IsActived.ACTIVE);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("markerCode", mm -> mm.ignoreCase().contains())
                .withMatcher("markerType", mm -> mm.ignoreCase())
                .withMatcher("userId",   mm -> mm.ignoreCase())
                .withIgnoreNullValues()
                .withIgnorePaths("printablePdfUrl",
                "physicalWidthM",
                "createdAt",
                "updatedAt"
        );

        Example<Marker> example = Example.of(probe, matcher);

        // 1) Không truyền pageId -> giữ logic cũ
        if (pageId == null || pageId.isBlank()) {
            return repo.findAll(example, pageable).map(mapper::toResponse);
        }

        // 2) Có pageId -> lấy markerId từ bảng page_markers rồi query marker theo markerIds
        List<PageMarker> links = pageMarkerRepo.findAllByPageId(pageId);
        if (links.isEmpty()) {
            return Page.empty(pageable);
        }

        Set<String> markerIds = links.stream()
                .map(PageMarker::getMarkerId)
                .collect(Collectors.toSet());

        // Lấy tất cả marker ACTIVE có id trong markerIds với paging DB
        Page<Marker> page = repo.findByMarkerIdInAndIsActived(markerIds, IsActived.ACTIVE, pageable);

        // Nếu có truyền thêm markerCode / markerType thì filter tiếp trong memory
        Stream<Marker> stream = page.getContent().stream();

        if (markerCode != null && !markerCode.isBlank()) {
            String mc = markerCode.trim().toLowerCase();
            stream = stream.filter(m ->
                    m.getMarkerCode() != null &&
                            m.getMarkerCode().toLowerCase().contains(mc));
        }

        if (markerType != null && !markerType.isBlank()) {
            String mt = markerType.trim().toLowerCase();
            stream = stream.filter(m ->
                    m.getMarkerType() != null &&
                            m.getMarkerType().toLowerCase().equals(mt));
        }

        List<MarkerResponse> content = stream
                .map(mapper::toResponse)
                .toList();

        // totalElements: dùng tổng số marker ACTIVE theo pageId (trước khi filter thêm code/type)
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


}

