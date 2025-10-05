package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.PageRequestDTO;
import com.sep.rookieservice.dto.PageResponseDTO;
import com.sep.rookieservice.entity.Page;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PageMapper {

    PageResponseDTO toDto(Page entity);

    @Mapping(target = "pageId", ignore = true)
    Page toEntity(PageRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Page toEntity(PageRequestDTO dto, @MappingTarget Page entity);
}
