package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.ContractRequestDTO;
import com.sep.rookieservice.dto.ContractResponseDTO;
import com.sep.rookieservice.enums.ContractStatus;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContractService {

    ContractResponseDTO create(ContractRequestDTO dto);
    ContractResponseDTO update(String id, ContractRequestDTO dto);
    ContractResponseDTO getById(String id);
    void delete(String id);
    ContractResponseDTO changeStatus(String id, ContractStatus status);

    Page<ContractResponseDTO> search(String q, ContractStatus status, IsActived isActived, Pageable pageable);
}
