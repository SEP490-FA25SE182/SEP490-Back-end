package com.sep.arservice.service;

import com.sep.arservice.dto.ARSceneRequest;
import com.sep.arservice.dto.ARSceneResponse;
import com.sep.arservice.dto.ARSceneWithItemsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ARSceneService {
    ARSceneResponse create(ARSceneRequest req);
    ARSceneResponse update(String sceneId, ARSceneRequest req);
    void deleteHard(String sceneId);
    ARSceneResponse getById(String sceneId);
    Page<ARSceneResponse> search(String markerId, String status, Pageable pageable);

    // Dùng cho Unity: lấy scene + items + assets theo markerId không status
    ARSceneWithItemsResponse getLatestByMarkerId(String markerId);

    // Dùng cho Unity: lấy scene + items + assets theo markerId
    ARSceneWithItemsResponse getPublishedByMarkerId(String markerId);

    ARSceneWithItemsResponse getPublishedByAprilTag(String bookId, String family, int tagId);
    List<ARSceneWithItemsResponse> getPublishedManifestByBook(String bookId);
}
