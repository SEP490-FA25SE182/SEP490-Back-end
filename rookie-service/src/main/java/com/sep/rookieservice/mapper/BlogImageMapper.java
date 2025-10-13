package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.BlogImageRequest;
import com.sep.rookieservice.dto.BlogImageResponse;
import com.sep.rookieservice.entity.BlogImage;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BlogImageMapper {
    BlogImageResponse toResponse(BlogImage entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "imageUrl", source = "imageUrl"),
            @Mapping(target = "altText", source = "altText"),
            @Mapping(target = "position", source = "position"),
            @Mapping(target = "blogId", source = "blogId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(BlogImageRequest req, @MappingTarget BlogImage entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "imageUrl", source = "imageUrl"),
            @Mapping(target = "altText", source = "altText"),
            @Mapping(target = "position", source = "position"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(BlogImageRequest req, @MappingTarget BlogImage entity);
}
