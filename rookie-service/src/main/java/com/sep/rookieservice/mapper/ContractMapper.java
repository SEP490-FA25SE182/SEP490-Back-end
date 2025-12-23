package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.ContractRequestDTO;
import com.sep.rookieservice.dto.ContractResponseDTO;
import com.sep.rookieservice.entity.Contract;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContractMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "documentUrls", source = "documentUrls")
    ContractResponseDTO toDto(Contract entity);

    @Mapping(target = "contractId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActived", ignore = true)
    @Mapping(target = "status", ignore = true)
    void copyForCreate(ContractRequestDTO dto, @MappingTarget Contract entity);


    @Mapping(target = "contractId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActived", ignore = true)
    @Mapping(target = "status", ignore = true)
    void copyForUpdate(ContractRequestDTO dto, @MappingTarget Contract entity);
}