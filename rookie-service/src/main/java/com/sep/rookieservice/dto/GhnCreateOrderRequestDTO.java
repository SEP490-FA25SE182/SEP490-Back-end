package com.sep.rookieservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class GhnCreateOrderRequestDTO {

    private Integer payment_type_id;
    private String note;
    private String required_note;

    private String return_phone;
    private String return_address;
    private String return_district_name;
    private String return_ward_name;
    private String return_province_name;

    private String client_order_code;

    private String from_name;
    private String from_phone;
    private String from_address;
    private String from_ward_name;
    private String from_district_name;
    private String from_province_name;

    private String to_name;
    private String to_phone;
    private String to_address;
    private String to_ward_name;
    private String to_district_name;
    private String to_province_name;

    private Integer cod_amount;
    private String content;

    private Integer length;
    private Integer width;
    private Integer height;
    private Integer weight;

    private Integer pick_station_id;
    private Integer deliver_station_id;
    private Integer insurance_value;
    private Integer service_type_id;
    private String coupon;

    private Long pickup_time;
    private List<Integer> pick_shift;

    private List<GhnItemDTO> items;

    @Data
    public static class GhnItemDTO {
        private String name;
        private String code;
        private Integer quantity;
        private Integer price;

        private Integer length;
        private Integer width;
        private Integer height;
        private Integer weight;

        private Category category;

        @Data
        public static class Category {
            private String level1;
            private String level2;
            private String level3;
        }
    }
}
