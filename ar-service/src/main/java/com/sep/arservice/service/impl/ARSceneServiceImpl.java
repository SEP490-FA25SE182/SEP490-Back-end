package com.sep.arservice.service.impl;

import com.sep.arservice.dto.*;
import com.sep.arservice.enums.IsActived;
import com.sep.arservice.mapper.ARSceneItemMapper;
import com.sep.arservice.mapper.ARSceneMapper;
import com.sep.arservice.mapper.Asset3DMapper;
import com.sep.arservice.mapper.MarkerMapper;
import com.sep.arservice.model.ARScene;
import com.sep.arservice.model.ARSceneItem;
import com.sep.arservice.model.Asset3D;
import com.sep.arservice.model.Marker;
import com.sep.arservice.repository.ARSceneItemRepository;
import com.sep.arservice.repository.ARSceneRepository;
import com.sep.arservice.repository.Asset3DRepository;
import com.sep.arservice.repository.MarkerRepository;
import com.sep.arservice.service.ARSceneService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ARSceneServiceImpl implements ARSceneService {

    private final ARSceneRepository repo;
    private final ARSceneItemRepository itemRepo;
    private final MarkerRepository markerRepo;
    private final Asset3DRepository assetRepo;

    @Qualifier("ARSceneMapper")
    private final ARSceneMapper mapper;

    @Qualifier("ARSceneItemMapper")
    private final ARSceneItemMapper itemMapper;

    private final MarkerMapper markerMapper;

    @Qualifier("asset3DMapper")
    private final Asset3DMapper assetMapper;

    @Override
    public ARSceneResponse create(ARSceneRequest req) {
        // đảm bảo marker tồn tại
        markerRepo.findById(req.getMarkerId())
                .orElseThrow(() -> new RuntimeException("Marker not found: " + req.getMarkerId()));

        ARScene e = new ARScene();
        mapper.copyForCreate(req, e);
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public ARSceneResponse update(String sceneId, ARSceneRequest req) {
        ARScene e = repo.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("ARScene not found: " + sceneId));
        mapper.copyForUpdate(req, e);
        e.setUpdatedAt(Instant.now());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public void deleteHard(String sceneId) {
        repo.delete(repo.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("ARScene not found: " + sceneId)));
    }

    @Override
    @Transactional(readOnly = true)
    public ARSceneResponse getById(String sceneId) {
        return repo.findById(sceneId).map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("ARScene not found: " + sceneId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ARSceneResponse> search(String markerId, String status, Pageable pageable) {
        ARScene probe = new ARScene();
        if (markerId != null && !markerId.isBlank()) probe.setMarkerId(markerId.trim());
        if (status   != null && !status.isBlank())   probe.setStatus(status.trim());

        ExampleMatcher m = ExampleMatcher.matchingAll()
                .withMatcher("markerId", mm -> mm.ignoreCase())
                .withMatcher("status",   mm -> mm.ignoreCase())
                .withIgnoreNullValues();

        return repo.findAll(Example.of(probe, m), pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ARSceneWithItemsResponse getPublishedByMarkerCode(String markerCode) {
        Marker marker = markerRepo.findByMarkerCodeIgnoreCaseAndIsActived(markerCode, IsActived.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Marker not found by code: " + markerCode));

        Optional<ARScene> latest = repo
                .findTopByMarkerIdAndStatusOrderByCreatedAtDesc(marker.getMarkerId(), "PUBLISHED");

        ARScene scene = latest.orElseThrow(() -> new RuntimeException("No PUBLISHED scene for marker: " + markerCode));

        // Items
        List<ARSceneItem> items = itemRepo.findBySceneIdOrderByOrderIndexAsc(scene.getSceneId());

        // Asset ids
        Set<String> assetIds = items.stream().map(ARSceneItem::getAsset3DId).collect(Collectors.toSet());
        List<Asset3D> assets = assetRepo.findAllById(assetIds);

        // Map to DTO
        ARSceneResponse sceneDto      = mapper.toResponse(scene);
        MarkerResponse markerDto      = markerMapper.toResponse(marker);
        List<ARSceneItemResponse> itemDtos = items.stream().map(itemMapper::toResponse).toList();
        List<Asset3DResponse> assetDtos    = assets.stream().map(assetMapper::toResponse).toList();

        return mapper.compose(sceneDto, markerDto, assetDtos, itemDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public ARSceneWithItemsResponse getPublishedByMarkerId(String markerId) {
        // đảm bảo marker tồn tại
        Marker marker = markerRepo.findById(markerId)
                .orElseThrow(() -> new RuntimeException("Marker not found: " + markerId));

        // Lấy scene PUBLISHED mới nhất theo markerId
        Optional<ARScene> latest = repo
                .findTopByMarkerIdAndStatusOrderByCreatedAtDesc(markerId, "PUBLISHED");

        ARScene scene = latest
                .orElseThrow(() -> new RuntimeException("No PUBLISHED scene for markerId: " + markerId));

        // Items
        List<ARSceneItem> items = itemRepo.findBySceneIdOrderByOrderIndexAsc(scene.getSceneId());

        // Asset ids
        Set<String> assetIds = items.stream()
                .map(ARSceneItem::getAsset3DId)
                .collect(Collectors.toSet());
        List<Asset3D> assets = assetRepo.findAllById(assetIds);

        // Map to DTO
        ARSceneResponse sceneDto           = mapper.toResponse(scene);
        MarkerResponse markerDto           = markerMapper.toResponse(marker);
        List<ARSceneItemResponse> itemDtos = items.stream()
                .map(itemMapper::toResponse)
                .toList();
        List<Asset3DResponse> assetDtos    = assets.stream()
                .map(assetMapper::toResponse)
                .toList();

        return mapper.compose(sceneDto, markerDto, assetDtos, itemDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public ARSceneWithItemsResponse getPublishedByAprilTag(String bookId, String family, int tagId) {
        Marker marker = markerRepo
                .findByBookIdAndTagFamilyAndTagIdAndIsActived(bookId, family, tagId, IsActived.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Marker not found: " + bookId + " / " + family + " / " + tagId));

        ARScene scene = repo
                .findTopByMarkerIdAndStatusOrderByCreatedAtDesc(marker.getMarkerId(), "PUBLISHED")
                .orElseThrow(() -> new RuntimeException("No PUBLISHED scene for marker: " + marker.getMarkerId()));

        List<ARSceneItem> items = itemRepo.findBySceneIdOrderByOrderIndexAsc(scene.getSceneId());

        Set<String> assetIds = items.stream()
                .map(ARSceneItem::getAsset3DId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Asset3D> assets = assetIds.isEmpty() ? List.of() : assetRepo.findAllById(assetIds);

        ARSceneResponse sceneDto = mapper.toResponse(scene);
        MarkerResponse markerDto = markerMapper.toResponse(marker);
        List<ARSceneItemResponse> itemDtos = items.stream().map(itemMapper::toResponse).toList();
        List<Asset3DResponse> assetDtos = assets.stream().map(assetMapper::toResponse).toList();

        return mapper.compose(sceneDto, markerDto, assetDtos, itemDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ARSceneWithItemsResponse> getPublishedManifestByBook(String bookId) {
        // load tất cả marker active của book
        List<Marker> markers = markerRepo.findAllByBookIdAndIsActived(bookId, IsActived.ACTIVE);
        if (markers.isEmpty()) return List.of();

        // lấy latest published scene cho từng marker (N+1 ít vì mỗi book ~10)
        // tối ưu sau nếu cần
        List<ARSceneWithItemsResponse> result = new ArrayList<>();
        for (Marker m : markers) {
            Optional<ARScene> latest = repo.findTopByMarkerIdAndStatusOrderByCreatedAtDesc(m.getMarkerId(), "PUBLISHED");
            if (latest.isEmpty()) continue;
            ARScene s = latest.get();

            List<ARSceneItem> items = itemRepo.findBySceneIdOrderByOrderIndexAsc(s.getSceneId());
            Set<String> assetIds = items.stream()
                    .map(ARSceneItem::getAsset3DId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            List<Asset3D> assets = assetIds.isEmpty() ? List.of() : assetRepo.findAllById(assetIds);

            result.add(mapper.compose(
                    mapper.toResponse(s),
                    markerMapper.toResponse(m),
                    assets.stream().map(assetMapper::toResponse).toList(),
                    items.stream().map(itemMapper::toResponse).toList()
            ));
        }
        return result;
    }

}
