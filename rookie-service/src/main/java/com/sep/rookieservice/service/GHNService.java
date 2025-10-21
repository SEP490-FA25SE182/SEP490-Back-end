package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.*;

public interface GHNService {
    GHNFeeResponse calculateFee(GHNFeeRequest request);
    GHNOrderInfoResponse getOrderInfo(GHNOrderInfoRequest request);
}
