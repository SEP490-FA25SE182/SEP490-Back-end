package com.sep.arservice.service.impl;

import com.sep.arservice.dto.AlignmentDataRequest;
import com.sep.arservice.dto.AlignmentDataResponse;
import com.sep.arservice.dto.AlignmentDataSearchRequest;
import com.sep.arservice.enums.IsActived;
import com.sep.arservice.mapper.AlignmentDataMapper;
import com.sep.arservice.model.AlignmentData;
import com.sep.arservice.model.Marker;
import com.sep.arservice.repository.AlignmentDataRepository;
import com.sep.arservice.repository.MarkerRepository;
import com.sep.arservice.service.AlignmentDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlignmentDataServiceImpl implements AlignmentDataService {

    private final AlignmentDataRepository repo;
    private final MarkerRepository markerRepo;
    private final AlignmentDataMapper mapper;

    @Override
    public AlignmentDataResponse create(AlignmentDataRequest req) {
        markerRepo.findById(req.getMarkerId())
                .orElseThrow(() -> new RuntimeException("Marker not found: " + req.getMarkerId()));

        AlignmentData e = new AlignmentData();
        mapper.copyForCreate(req, e);
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public List<AlignmentDataResponse> createBatch(List<AlignmentDataRequest> reqs) {
        List<AlignmentData> list = reqs.stream().map(r -> {
            markerRepo.findById(r.getMarkerId())
                    .orElseThrow(() -> new RuntimeException("Marker not found: " + r.getMarkerId()));
            AlignmentData e = new AlignmentData();
            mapper.copyForCreate(r, e);
            return e;
        }).toList();

        return repo.saveAll(list).stream().map(mapper::toResponse).toList();
    }

    @Override
    public AlignmentDataResponse update(String id, AlignmentDataRequest req) {
        AlignmentData e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("AlignmentData not found: " + id));
        mapper.copyForUpdate(req, e);
        e.setUpdatedAt(Instant.now());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public void deleteHard(String id) {
        repo.delete(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("AlignmentData not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public AlignmentDataResponse getById(String id) {
        return repo.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("AlignmentData not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlignmentDataResponse> search(AlignmentDataSearchRequest filter, Pageable pageable) {
        // Có 3 trường hợp:
        // 1) có markerId + from/to -> dùng between
        // 2) chỉ có markerId -> findAll by marker
        // 3) không filter -> trả về tất cả (page)
        if (filter != null && filter.getMarkerId() != null && !filter.getMarkerId().isBlank()) {
            String markerId = filter.getMarkerId().trim();
            if (filter.getFrom() != null && filter.getTo() != null) {
                List<AlignmentData> list = repo.findByMarkerIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        markerId, filter.getFrom(), filter.getTo()
                );
                return toPage(list, pageable).map(mapper::toResponse);
            } else {
                List<AlignmentData> list = repo.findByMarkerIdOrderByCreatedAtDesc(markerId);
                return toPage(list, pageable).map(mapper::toResponse);
            }
        }
        return repo.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AlignmentDataResponse latestByMarkerId(String markerId) {
        AlignmentData e = repo.findTopByMarkerIdOrderByCreatedAtDesc(markerId)
                .orElseThrow(() -> new RuntimeException("No alignment for markerId: " + markerId));
        return mapper.toResponse(e);
    }

    @Override
    @Transactional(readOnly = true)
    public AlignmentDataResponse latestByMarkerCode(String markerCode) {
        Marker m = markerRepo.findByMarkerCodeIgnoreCaseAndIsActived(markerCode, IsActived.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Marker not found by code: " + markerCode));
        return latestByMarkerId(m.getMarkerId());
    }

    // helper: list -> page
    private static <T> Page<T> toPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        List<T> sub = start > end ? List.of() : list.subList(start, end);
        return new PageImpl<>(sub, pageable, list.size());
    }
}
