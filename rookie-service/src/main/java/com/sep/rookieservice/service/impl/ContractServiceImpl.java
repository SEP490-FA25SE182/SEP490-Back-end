package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.ContractRequestDTO;
import com.sep.rookieservice.dto.ContractResponseDTO;
import com.sep.rookieservice.entity.Contract;
import com.sep.rookieservice.enums.ContractStatus;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.exception.ResourceNotFoundException;
import com.sep.rookieservice.mapper.ContractMapper;
import com.sep.rookieservice.repository.ContractRepository;
import com.sep.rookieservice.service.ContractService;
import com.sep.rookieservice.specification.ContractSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository repo;
    private final ContractMapper mapper;

    @Override
    public ContractResponseDTO create(ContractRequestDTO dto) {
        Contract entity = new Contract();

        if (dto.getContractNumber() != null && repo.existsByContractNumber(dto.getContractNumber())) {
            throw new IllegalArgumentException("Contract number already exists");
        }

        mapper.copyForCreate(dto, entity);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        return mapper.toDto(repo.save(entity));
    }

    @Override
    public ContractResponseDTO update(String id, ContractRequestDTO dto) {
        Contract entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        mapper.copyForUpdate(dto, entity);
        entity.setUpdatedAt(Instant.now());

        return mapper.toDto(repo.save(entity));
    }

    @Override
    public ContractResponseDTO getById(String id) {
        Contract entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        return mapper.toDto(entity);
    }

    @Override
    public void delete(String id) {
        Contract entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        entity.setIsActived(IsActived.INACTIVE);
        entity.setUpdatedAt(Instant.now());
        repo.save(entity);
    }

    @Override
    public ContractResponseDTO changeStatus(String id, ContractStatus status) {
        Contract entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        entity.setStatus(status);
        entity.setUpdatedAt(Instant.now());

        return mapper.toDto(repo.save(entity));
    }

    @Override
    public Page<ContractResponseDTO> search(String q, ContractStatus status, IsActived isActived, Pageable pageable) {
        var spec = ContractSpecification.buildSpecification(q, status, isActived);
        return repo.findAll(spec, pageable).map(mapper::toDto);
    }
}
