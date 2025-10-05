package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.ChapterRequestDTO;
import com.sep.rookieservice.dto.ChapterResponseDTO;
import com.sep.rookieservice.entity.Chapter;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    ChapterResponseDTO toDto(Chapter entity);

    @Mapping(target = "chapterId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Chapter toNewEntity(ChapterRequestDTO dto);

    @Mapping(target = "chapterId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Chapter toEntity(ChapterRequestDTO dto, @MappingTarget Chapter entity);
}

