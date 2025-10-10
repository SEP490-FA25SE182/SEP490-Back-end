package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.BlogRequest;
import com.sep.rookieservice.dto.BlogResponse;
import com.sep.rookieservice.entity.Blog;
import com.sep.rookieservice.entity.Tag;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BlogMapper {

    @Mapping(target = "tagIds", expression = "java(toTagIds(entity))")
    @Mapping(target = "tagNames", expression = "java(toTagNames(entity))")
    BlogResponse toResponse(Blog entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "authorId", source = "authorId"),
            @Mapping(target = "bookId", source = "bookId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(BlogRequest req, @MappingTarget Blog entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "bookId", source = "bookId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(BlogRequest req, @MappingTarget Blog entity);

    // helper
    default Set<String> toTagIds(Blog entity) {
        if (entity.getTags() == null) return null;
        return entity.getTags().stream().map(Tag::getTagId).collect(Collectors.toSet());
    }
    default Set<String> toTagNames(Blog entity) {
        if (entity.getTags() == null) return null;
        return entity.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
    }
}
