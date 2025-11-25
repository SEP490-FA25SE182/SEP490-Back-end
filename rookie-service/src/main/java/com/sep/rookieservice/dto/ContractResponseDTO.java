package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.ContractStatus;
import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class ContractResponseDTO {

    private String contractId;
    private String contractNumber;
    private String title;
    private String description;
    private String documentUrl;
    private Instant startDate;
    private Instant endDate;
    private ContractStatus status;
    private IsActived isActived;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;
}
