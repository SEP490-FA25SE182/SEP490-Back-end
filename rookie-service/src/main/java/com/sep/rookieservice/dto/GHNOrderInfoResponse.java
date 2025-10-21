package com.sep.rookieservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class GHNOrderInfoResponse {
    private int code;
    private String message;
    private DataResponse data;

    @Data
    public static class DataResponse {
        private String _id;
        private String order_code;
        private Detail detail;
        private List<Payment> payment;
        private String cod_collect_date;
        private String transaction_id;
        private String created_ip;
        private String created_date;
        private String updated_ip;
        private Integer updated_client;
        private Integer updated_employee;
        private String updated_source;
        private String updated_date;

        @Data
        public static class Detail {
            private Integer main_service;
            private Integer insurance;
            private Integer station_do;
            private Integer station_pu;
            private Integer returned;
            private Integer r2s;
            private Integer coupon;
        }

        @Data
        public static class Payment {
            private Integer value;
            private Integer payment_type;
            private String paid_date;
            private String created_date;
        }
    }
}
