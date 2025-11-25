package com.sep.arservice.service;

import com.sep.arservice.dto.ARSceneItemRequest;
import com.sep.arservice.dto.ARSceneItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ARSceneItemService {
    List<ARSceneItemResponse> createBatch(List<ARSceneItemRequest> reqs);
    ARSceneItemResponse update(String itemId, ARSceneItemRequest req);
    void deleteHard(String itemId);
    ARSceneItemResponse getById(String itemId);
    Page<ARSceneItemResponse> search(String sceneId, String asset3DId, Pageable pageable);
}
