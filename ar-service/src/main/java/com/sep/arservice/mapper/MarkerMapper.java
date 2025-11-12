package com.sep.arservice.mapper;

import com.sep.arservice.dto.MarkerRequest;
import com.sep.arservice.dto.MarkerResponse;
import com.sep.arservice.dto.PageMarkerRequest;
import com.sep.arservice.dto.PageMarkerResponse;
import com.sep.arservice.model.Marker;
import com.sep.arservice.model.PageMarker;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MarkerMapper {
    MarkerResponse toResponse(Marker e);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target="markerCode",       source="markerCode"),
            @Mapping(target="markerType",       source="markerType"),
            @Mapping(target="imageUrl",         source="imageUrl"),
            @Mapping(target="physicalWidthM",   source="physicalWidthM"),
            @Mapping(target="printablePdfUrl",  source="printablePdfUrl")
    })
    void copyForCreate(MarkerRequest req, @MappingTarget Marker e);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target="markerType",       source="markerType"),
            @Mapping(target="imageUrl",         source="imageUrl"),
            @Mapping(target="physicalWidthM",   source="physicalWidthM"),
            @Mapping(target="printablePdfUrl",  source="printablePdfUrl")
    })
    void copyForUpdate(MarkerRequest req, @MappingTarget Marker e);
}



