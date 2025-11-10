package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.UserRequest;
import com.sep.rookieservice.dto.UserResponse;
import com.sep.rookieservice.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    // Entity -> Response
    @Mapping(target = "updatedAt", source = "updateAt")
    UserResponse toResponse(User entity);

    // Request -> Entity (CHO CREATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "fullName", source = "fullName"),
            @Mapping(target = "birthDate", source = "birthDate"),
            @Mapping(target = "gender", source = "gender"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "phoneNumber", source = "phoneNumber"),
            @Mapping(target = "avatarUrl", source = "avatarUrl"),
            @Mapping(target = "roleId", source = "roleId"),
            @Mapping(target = "royalty", source = "royalty"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(UserRequest req, @MappingTarget User entity);

    // Request -> Entity (CHO UPDATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "fullName", source = "fullName"),
            @Mapping(target = "birthDate", source = "birthDate"),
            @Mapping(target = "gender", source = "gender"),
            @Mapping(target = "phoneNumber", source = "phoneNumber"),
            @Mapping(target = "avatarUrl", source = "avatarUrl"),
            @Mapping(target = "royalty", source = "royalty"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(UserRequest req, @MappingTarget User entity);

}
