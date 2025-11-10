package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.GhnShippingFeeRequestDTO;
import com.sep.rookieservice.dto.GhnShippingFeeResponseDTO;

public interface GhnService {
    GhnShippingFeeResponseDTO calculateShippingFee(GhnShippingFeeRequestDTO requestDTO);
}
