package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.PageRequestDTO;
import com.sep.rookieservice.dto.PageResponseDTO;
import com.sep.rookieservice.entity.Page;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PageMapper {

    @Mappings({
            @Mapping(target = "pageId",     source = "pageId"),
            @Mapping(target = "chapterId",  source = "chapterId"),
            @Mapping(target = "pageNumber", source = "pageNumber"),
            @Mapping(target = "content",    source = "content"),
            @Mapping(target = "pageType",   source = "pageType"),
            @Mapping(target = "isActived",  source = "isActived"),
            @Mapping(target = "createdAt",  source = "createdAt"),
            @Mapping(target = "updatedAt",  source = "updatedAt")
    })
    PageResponseDTO toDto(Page entity);

    @Mapping(target = "pageId", ignore = true)
    Page toEntity(PageRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntity(PageRequestDTO dto, @MappingTarget Page entity);
}

