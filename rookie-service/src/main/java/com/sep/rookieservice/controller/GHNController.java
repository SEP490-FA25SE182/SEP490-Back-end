package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.*;
import com.sep.rookieservice.service.GHNService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ghn")
@RequiredArgsConstructor
public class GHNController {

    private final GHNService ghnService;

    @PostMapping("/calculate-fee")
    public GHNFeeResponse calculateFee(@RequestBody GHNFeeRequest request) {
        return ghnService.calculateFee(request);
    }

    @PostMapping("/order-info")
    public GHNOrderInfoResponse getOrderInfo(@RequestBody GHNOrderInfoRequest request) {
        return ghnService.getOrderInfo(request);
    }
}
