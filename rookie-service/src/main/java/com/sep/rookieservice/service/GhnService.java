package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.GhnCreateOrderRequestDTO;
import com.sep.rookieservice.dto.GhnCreateOrderResponseDTO;
import com.sep.rookieservice.dto.GhnShippingFeeRequestDTO;
import com.sep.rookieservice.dto.GhnShippingFeeResponseDTO;

public interface GhnService {
    GhnShippingFeeResponseDTO calculateShippingFee(GhnShippingFeeRequestDTO requestDTO);
    GhnCreateOrderResponseDTO createOrder(GhnCreateOrderRequestDTO requestDTO);
}
