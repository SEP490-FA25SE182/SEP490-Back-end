package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.ContractStatus;
import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class ContractResponseDTO {

    private String contractId;
    private String contractNumber;
    private String title;
    private String description;
    private List<String> documentUrls;
    private Instant startDate;
    private Instant endDate;
    private ContractStatus status;
    private IsActived isActived;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;
    private String userId;
}
