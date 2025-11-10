package com.sep.rookieservice.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class GhnProvinceDTO {
    private Integer ProvinceID;
    private String ProvinceName;
    private Integer CountryID;
    private List<String> NameExtension;
    private String CanUpdateCOD;
    private Integer Status;
    private Instant CreatedAt;
    private Instant UpdatedAt;
}
