package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.CartItemRequest;
import com.sep.rookieservice.dto.CartItemResponse;
import com.sep.rookieservice.entity.CartItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CartItemMapper {

    CartItemResponse toResponse(CartItem entity);

    // CREATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "price", source = "price")
    })
    void copyForCreate(CartItemRequest req, @MappingTarget CartItem entity);

    // UPDATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "price", source = "price")
    })
    void copyForUpdate(CartItemRequest req, @MappingTarget CartItem entity);
}
