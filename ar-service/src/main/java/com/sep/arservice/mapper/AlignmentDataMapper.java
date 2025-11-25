package com.sep.arservice.mapper;

import com.sep.arservice.dto.AlignmentDataRequest;
import com.sep.arservice.dto.AlignmentDataResponse;
import com.sep.arservice.model.AlignmentData;
import org.mapstruct.*;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface AlignmentDataMapper {

    AlignmentDataResponse toResponse(AlignmentData e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "markerId",     source = "markerId")
    @Mapping(target = "poseMatrix",   source = "poseMatrix")
    @Mapping(target = "scale",        source = "scale")
    @Mapping(target = "confidenceScore", source = "confidenceScore")
    @Mapping(target = "rotation",     source = "rotation")
    @Mapping(target = "translation",  source = "translation")
    void copyForCreate(AlignmentDataRequest req, @MappingTarget AlignmentData e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "poseMatrix",   source = "poseMatrix")
    @Mapping(target = "scale",        source = "scale")
    @Mapping(target = "confidenceScore", source = "confidenceScore")
    @Mapping(target = "rotation",     source = "rotation")
    @Mapping(target = "translation",  source = "translation")
    void copyForUpdate(AlignmentDataRequest req, @MappingTarget AlignmentData e);

    @AfterMapping
    default void ensureTimestamps(@MappingTarget AlignmentData e, AlignmentDataRequest req){
        if (e.getCreatedAt() == null) {
            e.setCreatedAt(req.getCreatedAt() != null ? req.getCreatedAt() : Instant.now());
        }
        e.setUpdatedAt(Instant.now());
    }
}
