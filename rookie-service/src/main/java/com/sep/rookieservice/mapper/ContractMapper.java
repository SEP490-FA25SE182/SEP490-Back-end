package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.ContractRequestDTO;
import com.sep.rookieservice.dto.ContractResponseDTO;
import com.sep.rookieservice.entity.Contract;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContractMapper {

    @Mapping(target = "userId", source = "user.userId")
    ContractResponseDTO toDto(Contract entity);

    @Mapping(target = "contractId", ignore = true)
    @Mapping(target = "user", ignore = true)  // user sẽ set riêng trong service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActived", ignore = true)
    @Mapping(target = "status", ignore = true)  // status sẽ set mặc định trong service
    void copyForCreate(ContractRequestDTO dto, @MappingTarget Contract entity);


    @Mapping(target = "contractId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActived", ignore = true)
    @Mapping(target = "status", ignore = true)
    void copyForUpdate(ContractRequestDTO dto, @MappingTarget Contract entity);
}