package com.sep.arservice.mapper;

import com.sep.arservice.dto.PageMarkerRequest;
import com.sep.arservice.dto.PageMarkerResponse;
import com.sep.arservice.model.PageMarker;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PageMarkerMapper {
    PageMarkerResponse toResponse(PageMarker e);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target="pageId",   source="pageId"),
            @Mapping(target="markerId", source="markerId")
    })
    void copyForCreate(PageMarkerRequest req, @MappingTarget PageMarker e);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target="pageId",   source="pageId"),
            @Mapping(target="markerId", source="markerId")
    })
    void copyForUpdate(PageMarkerRequest req, @MappingTarget PageMarker e);
}
