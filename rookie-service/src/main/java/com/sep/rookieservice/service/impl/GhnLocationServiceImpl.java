package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.*;
import com.sep.rookieservice.service.GhnLocationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GhnLocationServiceImpl implements GhnLocationService {

    @Value("${ghn.base-url}")
    private String baseUrl;

    @Value("${ghn.token}")
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<GhnProvinceDTO> getProvinces() {
        System.out.println("GHN Base URL: " + baseUrl);
        System.out.println("GHN Token: " + token);
        String url = baseUrl + "/master-data/province";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GhnBaseResponse<List<GhnProvinceDTO>>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<GhnBaseResponse<List<GhnProvinceDTO>>>() {}
                );

        return response.getBody().getData();
    }

    @Override
    public List<GhnDistrictDTO> getDistricts(Integer provinceId) {
        String url = baseUrl + "/master-data/district";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"province_id\":" + provinceId + "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<GhnBaseResponse<List<GhnDistrictDTO>>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<GhnBaseResponse<List<GhnDistrictDTO>>>() {}
                );

        return response.getBody().getData();
    }

    @Override
    public List<GhnWardDTO> getWards(Integer districtId) {
        String url = baseUrl + "/master-data/ward?district_id=" + districtId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GhnBaseResponse<List<GhnWardDTO>>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<GhnBaseResponse<List<GhnWardDTO>>>() {}
                );

        return response.getBody().getData();
    }

    @PostConstruct
    public void init() {
        System.out.println("GHN Base URL: " + baseUrl);
        System.out.println("GHN Token: " + token);
    }
}