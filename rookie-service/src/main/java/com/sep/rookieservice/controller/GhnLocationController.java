package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.*;
import com.sep.rookieservice.service.GhnLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ghn")
@RequiredArgsConstructor
public class GhnLocationController {

    private final GhnLocationService ghnLocationService;

    @GetMapping("/provinces")
    public List<GhnProvinceDTO> getProvinces() {
        return ghnLocationService.getProvinces();
    }

    @GetMapping("/districts")
    public List<GhnDistrictDTO> getDistricts(@RequestParam Integer provinceId) {
        return ghnLocationService.getDistricts(provinceId);
    }

    @GetMapping("/wards")
    public List<GhnWardDTO> getWards(@RequestParam Integer districtId) {
        return ghnLocationService.getWards(districtId);
    }
}
