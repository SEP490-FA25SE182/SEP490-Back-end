package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.ContractStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class ContractRequestDTO {

    private String contractNumber;
    private String title;
    private String description;
    private String documentUrl;
    private Instant startDate;
    private Instant endDate;
    private ContractStatus status;
    private String note;
}
