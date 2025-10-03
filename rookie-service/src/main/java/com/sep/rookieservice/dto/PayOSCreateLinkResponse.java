package com.sep.rookieservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayOSCreateLinkResponse {
    private String code;     // "00" if success
    private String desc;     // "success"
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private String paymentLinkId;
        private int amount;
        private String description;
        private long orderCode;
        private long expiredAt;
        private String status;
        private String checkoutUrl;
        private String qrCode;
    }
}
