package com.sep.arservice.service;

import com.sep.arservice.dto.AlignmentDataRequest;
import com.sep.arservice.dto.AlignmentDataResponse;
import com.sep.arservice.dto.AlignmentDataSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AlignmentDataService {
    AlignmentDataResponse create(AlignmentDataRequest req);
    List<AlignmentDataResponse> createBatch(List<AlignmentDataRequest> reqs);

    AlignmentDataResponse update(String id, AlignmentDataRequest req);
    void deleteHard(String id);

    AlignmentDataResponse getById(String id);

    Page<AlignmentDataResponse> search(AlignmentDataSearchRequest filter, Pageable pageable);

    AlignmentDataResponse latestByMarkerId(String markerId);

    // tiện cho Unity/Flutter: lấy latest theo markerCode
    AlignmentDataResponse latestByMarkerCode(String markerCode);
}
