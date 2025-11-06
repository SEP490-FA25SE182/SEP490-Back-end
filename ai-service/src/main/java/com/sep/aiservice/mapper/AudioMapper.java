package com.sep.aiservice.mapper;

import com.sep.aiservice.dto.AudioRequest;
import com.sep.aiservice.dto.AudioResponse;
import com.sep.aiservice.entity.Audio;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AudioMapper {

    AudioResponse toResponse(Audio entity);

    // CREATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "audioUrl", source = "audioUrl"),
            @Mapping(target = "voice", source = "voice"),
            @Mapping(target = "format", source = "format"),
            @Mapping(target = "language", source = "language"),
            @Mapping(target = "durationMs", source = "durationMs"),
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(AudioRequest req, @MappingTarget Audio entity);

    // UPDATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "audioUrl", source = "audioUrl"),
            @Mapping(target = "voice", source = "voice"),
            @Mapping(target = "format", source = "format"),
            @Mapping(target = "language", source = "language"),
            @Mapping(target = "durationMs", source = "durationMs"),
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(AudioRequest req, @MappingTarget Audio entity);
}
