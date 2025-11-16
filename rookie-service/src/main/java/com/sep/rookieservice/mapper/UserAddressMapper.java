package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.UserAddressRequest;
import com.sep.rookieservice.dto.UserAddressResponse;
import com.sep.rookieservice.entity.UserAddress;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserAddressMapper {

    UserAddressResponse toResponse(UserAddress entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "addressInfor", source = "addressInfor"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "phoneNumber", source = "phoneNumber"),
            @Mapping(target = "fullName", source = "fullName"),
            @Mapping(target = "type", source = "type"),
            @Mapping(target = "default", source = "default"),
            @Mapping(target = "isActived", source = "isActived"),
            @Mapping(target = "provinceId", source = "provinceId"),
            @Mapping(target = "districtId", source = "districtId"),
            @Mapping(target = "wardCode", source = "wardCode")
    })
    void copyForCreate(UserAddressRequest req, @MappingTarget UserAddress entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "addressInfor", source = "addressInfor"),
            @Mapping(target = "phoneNumber", source = "phoneNumber"),
            @Mapping(target = "fullName", source = "fullName"),
            @Mapping(target = "type", source = "type"),
            @Mapping(target = "isActived", source = "isActived"),
            @Mapping(target = "provinceId", source = "provinceId"),
            @Mapping(target = "districtId", source = "districtId"),
            @Mapping(target = "wardCode", source = "wardCode")
    })
    void copyForUpdate(UserAddressRequest req, @MappingTarget UserAddress entity);
}