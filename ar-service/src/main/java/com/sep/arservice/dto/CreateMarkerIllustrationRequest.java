package com.sep.arservice.dto;

import lombok.Data;

@Data
public class CreateMarkerIllustrationRequest {
    private String markerId;
    private String illustrationImageUrl;
    /**
     * camoStrength: 0..1 (0 = tag chuẩn đen/trắng, 1 = tag “nhạt” hơn).
     * Khuyến nghị: 0.15 ~ 0.25 để vẫn detect tốt.
     */
    private Double camoStrength;

    /**
     * Quiet-zone overlay alpha 0..255 (trắng mờ).
     * Khuyến nghị: 160~220.
     */
    private Integer quietZoneAlpha;

    /**
     * Nếu muốn ép đúng “A5 max” theo dpi (để tính pixel tag size), mặc định 300.
     */
    private Integer assumedDpi;

    private Double grainStrength;

}
