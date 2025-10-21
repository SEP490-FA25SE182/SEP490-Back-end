package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.*;
import com.sep.rookieservice.service.GHNService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GHNServiceImpl implements GHNService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ghn.token}")
    private String token;

    @Value("${ghn.shop-id}")
    private String shopId;

    @Value("${ghn.base-url}")
    private String baseUrl;

    @Override
    public GHNFeeResponse calculateFee(GHNFeeRequest request) {
        String url = baseUrl + "/v2/shipping-order/fee";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.set("ShopId", shopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GHNFeeRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GHNFeeResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, GHNFeeResponse.class);

        return response.getBody();
    }

    @Override
    public GHNOrderInfoResponse getOrderInfo(GHNOrderInfoRequest request) {
        String url = baseUrl + "/v2/shipping-order/soc";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.set("ShopId", shopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GHNOrderInfoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GHNOrderInfoResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, GHNOrderInfoResponse.class);

        return response.getBody();
    }
}
