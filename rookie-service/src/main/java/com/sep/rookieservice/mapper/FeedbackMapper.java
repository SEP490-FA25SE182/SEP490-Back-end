package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.FeedbackRequestDTO;
import com.sep.rookieservice.dto.FeedbackResponseDTO;
import com.sep.rookieservice.entity.Feedback;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    FeedbackResponseDTO toDto(Feedback entity);

    @Mapping(target = "feedbackId", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "isActived", ignore = true)
    Feedback toNewEntity(FeedbackRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    void updateEntityFromDto(FeedbackRequestDTO dto, @MappingTarget Feedback entity);
}
