package com.sep.arservice.service;

import com.sep.arservice.dto.Asset3DGenerateRequest;
import com.sep.arservice.dto.Asset3DResponse;
import com.sep.arservice.dto.Asset3DUploadRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface Asset3DService {
    List<Asset3DResponse> getAll();
    Asset3DResponse getById(String id);
    Asset3DResponse upload(MultipartFile file, Asset3DUploadRequest meta) throws IOException;
    Asset3DResponse generate(Asset3DGenerateRequest req); // Meshy → Firebase → Asset3D
    void softDelete(String id);

    Page<Asset3DResponse> search(String markerId, String userId, String format, Pageable pageable);
    Page<Asset3DResponse> searchByMarkerCode(String markerCode, Pageable pageable);
    List<Asset3DResponse> latestByMarker(String markerId, int limit);
}

