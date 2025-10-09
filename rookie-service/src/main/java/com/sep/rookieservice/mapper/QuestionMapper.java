package com.sep.rookieservice.mapper;
import com.sep.rookieservice.dto.QuestionRequestDTO;
import com.sep.rookieservice.dto.QuestionResponseDTO;
import com.sep.rookieservice.entity.Question;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    Question toNewEntity(QuestionRequestDTO dto);

    void updateEntityFromDto(QuestionRequestDTO dto, @MappingTarget Question entity);

    QuestionResponseDTO toDto(Question entity);
}