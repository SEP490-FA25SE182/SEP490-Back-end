package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.AnswerRequestDTO;
import com.sep.rookieservice.dto.AnswerResponseDTO;
import com.sep.rookieservice.entity.Answer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    Answer toEntity(AnswerRequestDTO dto);

    AnswerResponseDTO toDto(Answer entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AnswerRequestDTO dto, @MappingTarget Answer entity);
}
