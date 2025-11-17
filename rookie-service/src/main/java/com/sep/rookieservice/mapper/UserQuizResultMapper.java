package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.UserQuizResultRequest;
import com.sep.rookieservice.dto.UserQuizResultResponse;
import com.sep.rookieservice.entity.UserQuizResult;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserQuizResultMapper {

    UserQuizResultResponse toDto(UserQuizResult entity);

    @Mapping(target = "resultId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "quiz", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserQuizResult toNewEntity(UserQuizResultRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "quiz", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(UserQuizResultRequest dto, @MappingTarget UserQuizResult entity);
}
