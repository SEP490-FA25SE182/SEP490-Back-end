package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.ContractStatus;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class ContractRequestDTO {

    private String contractNumber;
    private String title;
    private String description;
    private List<String> documentUrls;
    private Instant startDate;
    private Instant endDate;
    private ContractStatus status;
    private String note;
    private String userId;
}
