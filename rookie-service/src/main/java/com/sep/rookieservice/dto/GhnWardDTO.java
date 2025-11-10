package com.sep.rookieservice.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class GhnWardDTO {
    private String WardCode;
    private Integer DistrictID;
    private String WardName;
    private List<String> NameExtension;
    private String CanUpdateCOD;
    private Integer SupportType;
    private Integer Status;
    private Instant CreatedDate;
    private Instant UpdatedDate;
}
