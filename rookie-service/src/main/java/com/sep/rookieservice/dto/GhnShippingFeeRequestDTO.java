package com.sep.rookieservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class GhnShippingFeeRequestDTO {

    @JsonProperty("service_type_id")
    private Integer serviceTypeId;  // 2: Hàng nhẹ, 5: Hàng nặng

    @JsonProperty("from_district_id")
    private Integer fromDistrictId;

    @JsonProperty("from_ward_code")
    private String fromWardCode;

    @JsonProperty("to_district_id")
    private Integer toDistrictId;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    private Integer length;
    private Integer width;
    private Integer height;
    private Integer weight;

    @JsonProperty("insurance_value")
    private Integer insuranceValue;

    private String coupon;
    private Integer codFailedAmount;
    private Integer codValue;
    private List<GhnItemDTO> items;
}
