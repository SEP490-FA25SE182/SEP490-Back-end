package com.sep.aiservice.mapper;

import com.sep.aiservice.dto.AIGenerationTargetRequest;
import com.sep.aiservice.dto.AIGenerationTargetResponse;
import com.sep.aiservice.entity.AIGenerationTarget;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AIGenerationTargetMapper {

    AIGenerationTargetResponse toResponse(AIGenerationTarget entity);

    // CREATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "targetType", source = "targetType"),
            @Mapping(target = "aiGenerationId", source = "aiGenerationId"),
            @Mapping(target = "targetRefId", source = "targetRefId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(AIGenerationTargetRequest req, @MappingTarget AIGenerationTarget entity);

    // UPDATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "targetType", source = "targetType"),
            @Mapping(target = "aiGenerationId", source = "aiGenerationId"),
            @Mapping(target = "targetRefId", source = "targetRefId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(AIGenerationTargetRequest req, @MappingTarget AIGenerationTarget entity);
}
