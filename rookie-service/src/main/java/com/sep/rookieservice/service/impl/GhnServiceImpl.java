package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.config.GhnProperties;
import com.sep.rookieservice.dto.GhnShippingFeeRequestDTO;
import com.sep.rookieservice.dto.GhnShippingFeeResponseDTO;
import com.sep.rookieservice.service.GhnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GhnServiceImpl implements GhnService {

    private final GhnProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public GhnShippingFeeResponseDTO calculateShippingFee(GhnShippingFeeRequestDTO requestDTO) {
        String url = props.getBaseUrl() + "/v2/shipping-order/fee";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", props.getToken());
        headers.set("ShopId", props.getShopId());

        HttpEntity<GhnShippingFeeRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new RuntimeException("Không nhận được phản hồi từ GHN");
        }

        Map<String, Object> data = (Map<String, Object>) body.get("data");

        GhnShippingFeeResponseDTO dto = new GhnShippingFeeResponseDTO();
        dto.setMessage((String) body.get("message"));

        if (data != null) {
            dto.setTotal((Integer) data.get("total"));
            dto.setServiceFee((Integer) data.get("service_fee"));
            dto.setInsuranceFee((Integer) data.get("insurance_fee"));
            dto.setPickStationFee((Integer) data.get("pick_station_fee"));
            dto.setCouponValue((Integer) data.get("coupon_value"));
            dto.setR2sFee((Integer) data.get("r2s_fee"));
            dto.setDocumentReturn((Integer) data.get("document_return"));
            dto.setDoubleCheck((Integer) data.get("double_check"));
            dto.setCodFee((Integer) data.get("cod_fee"));
            dto.setPickRemoteAreasFee((Integer) data.get("pick_remote_areas_fee"));
            dto.setDeliverRemoteAreasFee((Integer) data.get("deliver_remote_areas_fee"));
            dto.setCodFailedFee((Integer) data.get("cod_failed_fee"));
        }

        return dto;
    }
}
