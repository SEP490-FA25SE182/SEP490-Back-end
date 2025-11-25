package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.ContractStatus;
import lombok.Data;

@Data
public class UpdateContractStatusRequest {
    private ContractStatus status;
}
