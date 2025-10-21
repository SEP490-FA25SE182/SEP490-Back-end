package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.UserAddressRequest;
import com.sep.rookieservice.dto.UserAddressResponse;
import com.sep.rookieservice.entity.UserAddress;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserAddressMapper {

    // Entity -> Response
    UserAddressResponse toResponse(UserAddress entity);

    // Request -> Entity (CREATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "addressInfor", source = "addressInfor"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(UserAddressRequest req, @MappingTarget UserAddress entity);

    // Request -> Entity (UPDATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "addressInfor", source = "addressInfor"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(UserAddressRequest req, @MappingTarget UserAddress entity);
}
