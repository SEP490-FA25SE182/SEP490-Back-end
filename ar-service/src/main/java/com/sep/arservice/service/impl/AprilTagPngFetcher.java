package com.sep.arservice.service.impl;

import com.sep.arservice.enums.AprilTagFamilySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AprilTagPngFetcher {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${apriltag.source.raw-base:https://raw.githubusercontent.com/AprilRobotics/apriltag-imgs/master}")
    private String rawBase;

    public byte[] fetchPng(String tagFamily, int tagId) {
        AprilTagFamilySpec spec = AprilTagFamilySpec.from(tagFamily);

        if (tagId < 0 || tagId > spec.maxId()) {
            throw new IllegalArgumentException(
                    "tagId out of range for " + spec.folder() + ": " + tagId + " (max=" + spec.maxId() + ")"
            );
        }

        String url = rawBase + "/" + spec.folder() + "/" + spec.fileName(tagId);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes("image/png,application/octet-stream"));

        ResponseEntity<byte[]> res = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), byte[].class
        );

        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null || res.getBody().length == 0) {
            throw new IllegalStateException("Cannot fetch AprilTag PNG: " + url + ", status=" + res.getStatusCode());
        }

        return res.getBody();
    }
}
