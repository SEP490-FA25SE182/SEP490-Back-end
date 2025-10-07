package com.sep.aiservice.mapper;

import com.sep.aiservice.dto.AIGenerationRequest;
import com.sep.aiservice.dto.AIGenerationResponse;
import com.sep.aiservice.entity.AIGeneration;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AIGenerationMapper {

    AIGenerationResponse toResponse(AIGeneration entity);

    // CREATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "modelName", source = "modelName"),
            @Mapping(target = "prompt", source = "prompt"),
            @Mapping(target = "negativePrompt", source = "negativePrompt"),
            @Mapping(target = "durationMs", source = "durationMs"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "mode", source = "mode"),
            @Mapping(target = "aspectRatio", source = "aspectRatio"),
            @Mapping(target = "strength", source = "strength"),
            @Mapping(target = "seed", source = "seed"),
            @Mapping(target = "cfgScale", source = "cfgScale"),
            @Mapping(target = "stylePreset", source = "stylePreset"),
            @Mapping(target = "acceptHeader", source = "acceptHeader"),
            @Mapping(target = "inputImageUrl", source = "inputImageUrl"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(AIGenerationRequest req, @MappingTarget AIGeneration entity);

    // UPDATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "modelName", source = "modelName"),
            @Mapping(target = "prompt", source = "prompt"),
            @Mapping(target = "negativePrompt", source = "negativePrompt"),
            @Mapping(target = "durationMs", source = "durationMs"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "mode", source = "mode"),
            @Mapping(target = "aspectRatio", source = "aspectRatio"),
            @Mapping(target = "strength", source = "strength"),
            @Mapping(target = "seed", source = "seed"),
            @Mapping(target = "cfgScale", source = "cfgScale"),
            @Mapping(target = "stylePreset", source = "stylePreset"),
            @Mapping(target = "acceptHeader", source = "acceptHeader"),
            @Mapping(target = "inputImageUrl", source = "inputImageUrl"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(AIGenerationRequest req, @MappingTarget AIGeneration entity);
}

