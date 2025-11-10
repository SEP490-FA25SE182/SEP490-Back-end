package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.config.GhnProperties;
import com.sep.rookieservice.dto.GhnDistrictDTO;
import com.sep.rookieservice.dto.GhnProvinceDTO;
import com.sep.rookieservice.dto.GhnWardDTO;
import com.sep.rookieservice.service.GhnAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GhnAddressServiceImpl implements GhnAddressService {

    private final GhnProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<GhnProvinceDTO> getProvinces() {
        String url = props.getBaseUrl() + "/master-data/province";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", props.getToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || body.get("data") == null)
            throw new RuntimeException("Không nhận được dữ liệu tỉnh/thành từ GHN");

        return (List<GhnProvinceDTO>) body.get("data");
    }

    @Override
    public List<GhnDistrictDTO> getDistricts(Integer provinceId) {
        String url = props.getBaseUrl() + "/master-data/district";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", props.getToken());

        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(Map.of("province_id", provinceId), headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || body.get("data") == null)
            throw new RuntimeException("Không nhận được dữ liệu quận/huyện từ GHN");

        return (List<GhnDistrictDTO>) body.get("data");
    }

    @Override
    public List<GhnWardDTO> getWards(Integer districtId) {
        String url = props.getBaseUrl() + "/master-data/ward?district_id=" + districtId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", props.getToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || body.get("data") == null)
            throw new RuntimeException("Không nhận được dữ liệu phường/xã từ GHN");

        return (List<GhnWardDTO>) body.get("data");
    }
}
