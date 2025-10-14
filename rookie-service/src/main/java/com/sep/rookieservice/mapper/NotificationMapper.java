package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.NotificationRequestDTO;
import com.sep.rookieservice.dto.NotificationResponseDTO;
import com.sep.rookieservice.entity.Notification;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "notificationId", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    Notification toEntity(NotificationRequestDTO dto);

    @Mapping(target = "notificationId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    void updateEntityFromDto(NotificationRequestDTO dto, @MappingTarget Notification entity);

    NotificationResponseDTO toDto(Notification entity);
}
