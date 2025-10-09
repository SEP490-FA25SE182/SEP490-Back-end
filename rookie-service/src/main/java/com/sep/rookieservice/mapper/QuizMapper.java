package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.QuizRequestDTO;
import com.sep.rookieservice.dto.QuizResponseDTO;
import com.sep.rookieservice.entity.Quiz;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    QuizResponseDTO toDto(Quiz entity);

    Quiz toNewEntity(QuizRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(QuizRequestDTO dto, @MappingTarget Quiz entity);
}
