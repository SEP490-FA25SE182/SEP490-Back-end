package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.ContractRequestDTO;
import com.sep.rookieservice.dto.ContractResponseDTO;
import com.sep.rookieservice.entity.Contract;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContractMapper {

    ContractResponseDTO toDto(Contract entity);

    void copyForCreate(ContractRequestDTO dto, @MappingTarget Contract entity);

    void copyForUpdate(ContractRequestDTO dto, @MappingTarget Contract entity);
}
