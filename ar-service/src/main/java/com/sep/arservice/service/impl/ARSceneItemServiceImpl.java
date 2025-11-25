package com.sep.arservice.service.impl;

import com.sep.arservice.dto.ARSceneItemRequest;
import com.sep.arservice.dto.ARSceneItemResponse;
import com.sep.arservice.mapper.ARSceneItemMapper;
import com.sep.arservice.model.ARSceneItem;
import com.sep.arservice.repository.ARSceneItemRepository;
import com.sep.arservice.repository.ARSceneRepository;
import com.sep.arservice.repository.Asset3DRepository;
import com.sep.arservice.service.ARSceneItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ARSceneItemServiceImpl implements ARSceneItemService {

    private final ARSceneItemRepository repo;
    private final ARSceneRepository sceneRepo;
    private final Asset3DRepository assetRepo;
    
    @Qualifier("ARSceneItemMapper")
    private final ARSceneItemMapper mapper;

    @Override
    public List<ARSceneItemResponse> createBatch(List<ARSceneItemRequest> reqs) {
        // validate scene & asset tồn tại (nhẹ)
        for (ARSceneItemRequest r : reqs) {
            sceneRepo.findById(r.getSceneId())
                    .orElseThrow(() -> new RuntimeException("ARScene not found: " + r.getSceneId()));
            assetRepo.findById(r.getAsset3DId())
                    .orElseThrow(() -> new RuntimeException("Asset3D not found: " + r.getAsset3DId()));
        }
        List<ARSceneItem> entities = reqs.stream().map(r -> {
            ARSceneItem e = new ARSceneItem();
            mapper.copyForCreate(r, e);
            e.setCreatedAt(Instant.now());
            e.setUpdatedAt(Instant.now());
            return e;
        }).toList();

        return repo.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    public ARSceneItemResponse update(String itemId, ARSceneItemRequest req) {
        ARSceneItem e = repo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("ARSceneItem not found: " + itemId));
        mapper.copyForUpdate(req, e);
        e.setUpdatedAt(Instant.now());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public void deleteHard(String itemId) {
        repo.delete(repo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("ARSceneItem not found: " + itemId)));
    }

    @Override
    @Transactional(readOnly = true)
    public ARSceneItemResponse getById(String itemId) {
        return repo.findById(itemId).map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("ARSceneItem not found: " + itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ARSceneItemResponse> search(String sceneId, String asset3DId, Pageable pageable) {
        Page<ARSceneItem> page;
        if (sceneId != null && !sceneId.isBlank() &&
                asset3DId != null && !asset3DId.isBlank()) {
            page = repo.findBySceneIdAndAsset3DIdOrderByOrderIndexAsc(
                    sceneId.trim(), asset3DId.trim(), pageable);
        } else if (sceneId != null && !sceneId.isBlank()) {
            List<ARSceneItem> list = repo.findBySceneIdOrderByOrderIndexAsc(sceneId.trim());
            page = new PageImpl<>(list, pageable, list.size());
        } else {
            page = repo.findAll(pageable);
        }
        return page.map(mapper::toResponse);
    }

}
