package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.CartRequest;
import com.sep.rookieservice.dto.CartResponse;
import com.sep.rookieservice.entity.Cart;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CartMapper {

    CartResponse toResponse(Cart entity);

    // CREATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "totalPrice", source = "totalPrice"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(CartRequest req, @MappingTarget Cart entity);

    // UPDATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(CartRequest req, @MappingTarget Cart entity);
}

