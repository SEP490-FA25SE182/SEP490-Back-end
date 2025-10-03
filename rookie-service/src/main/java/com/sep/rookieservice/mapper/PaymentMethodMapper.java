package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.PaymentMethodRequest;
import com.sep.rookieservice.dto.PaymentMethodResponse;
import com.sep.rookieservice.entity.PaymentMethod;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMethodMapper {
    PaymentMethodResponse toResponse(PaymentMethod e);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "methodName", source = "methodName"),
            @Mapping(target = "provider", source = "provider"),
            @Mapping(target = "decription", source = "decription"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(PaymentMethodRequest req, @MappingTarget PaymentMethod e);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "methodName", source = "methodName"),
            @Mapping(target = "provider", source = "provider"),
            @Mapping(target = "decription", source = "decription"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(PaymentMethodRequest req, @MappingTarget PaymentMethod e);
}