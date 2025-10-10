package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.TagRequest;
import com.sep.rookieservice.dto.TagResponse;
import com.sep.rookieservice.entity.Tag;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagMapper {
    TagResponse toResponse(Tag entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(TagRequest req, @MappingTarget Tag entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(TagRequest req, @MappingTarget Tag entity);
}
