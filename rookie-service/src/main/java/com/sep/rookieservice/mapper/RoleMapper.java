package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.RoleRequest;
import com.sep.rookieservice.dto.RoleResponse;
import com.sep.rookieservice.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    // Entity -> Response
    RoleResponse toResponse(Role entity);

    // Request -> Entity (CREATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "roleName", source = "roleName"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(RoleRequest req, @MappingTarget Role entity);

    // Request -> Entity (UPDATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "roleName", source = "roleName"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(RoleRequest req, @MappingTarget Role entity);
}
