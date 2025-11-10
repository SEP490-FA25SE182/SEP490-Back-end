package com.sep.aiservice.mapper;

import com.sep.aiservice.dto.PageIllustrationRequest;
import com.sep.aiservice.dto.PageIllustrationResponse;
import com.sep.aiservice.entity.PageIllustration;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PageIllustrationMapper {

    PageIllustrationResponse toResponse(PageIllustration entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "pageId", source = "pageId"),
            @Mapping(target = "illustrationId", source = "illustrationId")
    })
    void copyForCreate(PageIllustrationRequest req, @MappingTarget PageIllustration entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "pageId", source = "pageId"),
            @Mapping(target = "illustrationId", source = "illustrationId")
    })
    void copyForUpdate(PageIllustrationRequest req, @MappingTarget PageIllustration entity);
}

