package com.sep.aiservice.mapper;

import com.sep.aiservice.dto.IllustrationRequest;
import com.sep.aiservice.dto.IllustrationResponse;
import com.sep.aiservice.entity.Illustration;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IllustrationMapper {

    // Entity -> Response
    IllustrationResponse toResponse(Illustration entity);

    // Request -> Entity (CREATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "imageUrl", source = "imageUrl"),
            @Mapping(target = "style", source = "style"),
            @Mapping(target = "format", source = "format"),
            @Mapping(target = "width", source = "width"),
            @Mapping(target = "height", source = "height"),
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(IllustrationRequest req, @MappingTarget Illustration entity);

    // Request -> Entity (UPDATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "imageUrl", source = "imageUrl"),
            @Mapping(target = "style", source = "style"),
            @Mapping(target = "format", source = "format"),
            @Mapping(target = "width", source = "width"),
            @Mapping(target = "height", source = "height"),
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(IllustrationRequest req, @MappingTarget Illustration entity);
}
