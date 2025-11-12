package com.sep.arservice.dto;

import lombok.Data;

import java.util.List;

// Payload gộp để Unity tải 1 lần:
@Data
public class ARSceneWithItemsResponse {
    ARSceneResponse scene;
    MarkerResponse marker;                 // để lấy physical_width_m, image_url, marker_code
    List<Asset3DResponse> assets;          // meta asset (id, url, format,…)
    List<ARSceneItemResponse> items;       // transform cho từng asset
}
