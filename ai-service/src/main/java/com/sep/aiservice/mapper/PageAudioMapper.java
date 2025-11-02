package com.sep.aiservice.mapper;

import com.sep.aiservice.dto.PageAudioRequest;
import com.sep.aiservice.dto.PageAudioResponse;
import com.sep.aiservice.entity.PageAudio;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PageAudioMapper {

    PageAudioResponse toResponse(PageAudio entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "pageId",  source = "pageId"),
            @Mapping(target = "audioId", source = "audioId")
    })
    void copyForCreate(PageAudioRequest req, @MappingTarget PageAudio entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "pageId",  source = "pageId"),
            @Mapping(target = "audioId", source = "audioId")
    })
    void copyForUpdate(PageAudioRequest req, @MappingTarget PageAudio entity);
}

