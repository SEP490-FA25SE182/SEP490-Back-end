package com.sep.rookieservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GhnCreateOrderResponseDTO {

    private int code;
    private String message;

    @JsonProperty("message_display")
    private String messageDisplay;

    private DataResponse data;

    @Data
    public static class DataResponse {

        @JsonProperty("order_code")
        private String orderCode;

        @JsonProperty("sort_code")
        private String sortCode;

        @JsonProperty("trans_type")
        private String transType;

        @JsonProperty("ward_encode")
        private String wardEncode;

        @JsonProperty("district_encode")
        private String districtEncode;

        private Fee fee;

        @JsonProperty("total_fee")
        private Integer totalFee;

        @JsonProperty("expected_delivery_time")
        private String expectedDeliveryTime;

        @Data
        public static class Fee {

            @JsonProperty("main_service")
            private int mainService;

            private int insurance;

            @JsonProperty("station_do")
            private int stationDo;

            @JsonProperty("station_pu")
            private int stationPu;

            @JsonProperty("return")
            private int returnFee;

            private int r2s;
            private int coupon;

            @JsonProperty("cod_failed_fee")
            private int codFailedFee;
        }
    }
}
