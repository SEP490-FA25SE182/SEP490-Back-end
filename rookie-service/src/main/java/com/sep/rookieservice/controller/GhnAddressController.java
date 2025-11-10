package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.GhnProvinceDTO;
import com.sep.rookieservice.dto.GhnDistrictDTO;
import com.sep.rookieservice.dto.GhnWardDTO;
import com.sep.rookieservice.service.GhnAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/shipping")
@RequiredArgsConstructor
public class GhnAddressController {

    private final GhnAddressService ghnService;

    @GetMapping("/provinces")
    public List<GhnProvinceDTO> getProvinces() {
        return ghnService.getProvinces();
    }

    @GetMapping("/districts")
    public List<GhnDistrictDTO> getDistricts(@RequestParam Integer provinceId) {
        return ghnService.getDistricts(provinceId);
    }

    @GetMapping("/wards")
    public List<GhnWardDTO> getWards(@RequestParam Integer districtId) {
        return ghnService.getWards(districtId);
    }
}
