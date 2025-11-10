package com.sep.rookieservice.dto;

import lombok.Data;

@Data
public class GhnItemDTO {
    private String name;
    private String code;
    private Integer quantity;
    private Integer length;
    private Integer width;
    private Integer height;
    private Integer weight;
}
