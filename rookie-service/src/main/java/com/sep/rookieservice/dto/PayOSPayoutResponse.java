package com.sep.rookieservice.dto;

import lombok.Data;

@Data
public class PayOSPayoutResponse {
    private String code;   // "00" nếu tạo payout thành công
    private String desc;   // mô tả ngắn

    private DataObj data;  // payload chi tiết

    @lombok.Data
    public static class DataObj {
        private String approvalUrl; // URL để người dùng xác nhận/duyệt
        private String payoutId;    // id giao dịch payout
        private Long orderCode;
        private Integer amount;

    }
}

