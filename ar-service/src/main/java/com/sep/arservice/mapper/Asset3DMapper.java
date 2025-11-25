package com.sep.arservice.mapper;

import com.sep.arservice.dto.Asset3DResponse;
import com.sep.arservice.model.Asset3D;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface Asset3DMapper {

    @Mapping(target = "isActived", source = "isActived")
    Asset3DResponse toResponse(Asset3D e);
}
