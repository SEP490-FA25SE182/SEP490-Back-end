package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.GhnProvinceDTO;
import com.sep.rookieservice.dto.GhnDistrictDTO;
import com.sep.rookieservice.dto.GhnWardDTO;

import java.util.List;

public interface GhnAddressService {
    List<GhnProvinceDTO> getProvinces();
    List<GhnDistrictDTO> getDistricts(Integer provinceId);
    List<GhnWardDTO> getWards(Integer districtId);
}
