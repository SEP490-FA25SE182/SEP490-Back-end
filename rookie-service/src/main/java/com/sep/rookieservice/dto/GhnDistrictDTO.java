package com.sep.rookieservice.dto;

import lombok.Data;

@Data
public class GhnDistrictDTO {
    private Integer DistrictID;
    private Integer ProvinceID;
    private String DistrictName;
    private String Code;
    private Integer Type;
    private Integer SupportType;
}
