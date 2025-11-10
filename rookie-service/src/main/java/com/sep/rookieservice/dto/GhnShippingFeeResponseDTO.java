package com.sep.rookieservice.dto;

import lombok.Data;

@Data
public class GhnShippingFeeResponseDTO {
    private Integer total;
    private Integer serviceFee;
    private Integer insuranceFee;
    private Integer pickStationFee;
    private Integer couponValue;
    private Integer r2sFee;
    private Integer documentReturn;
    private Integer doubleCheck;
    private Integer codFee;
    private Integer pickRemoteAreasFee;
    private Integer deliverRemoteAreasFee;
    private Integer codFailedFee;
    private String message;
}
