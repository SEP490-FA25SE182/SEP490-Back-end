package com.sep.rookieservice.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class GhnDistrictDTO {
    private Integer DistrictID;
    private Integer ProvinceID;
    private String DistrictName;
    private Integer SupportType;
    private List<String> NameExtension;
    private String CanUpdateCOD;
    private Integer Status;
    private Instant CreatedDate;
    private Instant UpdatedDate;
}
