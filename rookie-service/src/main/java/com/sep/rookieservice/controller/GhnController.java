package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.GhnCreateOrderRequestDTO;
import com.sep.rookieservice.dto.GhnCreateOrderResponseDTO;
import com.sep.rookieservice.dto.GhnShippingFeeRequestDTO;
import com.sep.rookieservice.dto.GhnShippingFeeResponseDTO;
import com.sep.rookieservice.service.GhnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rookie/shipping")
@RequiredArgsConstructor
public class GhnController {

    private final GhnService ghnService;

    @PostMapping("/calculate-fee")
    public GhnShippingFeeResponseDTO calculateFee(@Valid @RequestBody GhnShippingFeeRequestDTO requestDTO) {
        return ghnService.calculateShippingFee(requestDTO);
    }

    @PostMapping("/create-order")
    public GhnCreateOrderResponseDTO createOrder(@Valid @RequestBody GhnCreateOrderRequestDTO requestDTO) {
        return ghnService.createOrder(requestDTO);
    }
}
