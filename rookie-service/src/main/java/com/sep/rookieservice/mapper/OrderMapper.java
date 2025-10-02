package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.OrderRequest;
import com.sep.rookieservice.dto.OrderResponse;
import com.sep.rookieservice.entity.Order;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    OrderResponse toResponse(Order entity);

    // CREATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "totalPrice", source = "totalPrice"),
            @Mapping(target = "status", source = "status")
    })
    void copyForCreate(OrderRequest req, @MappingTarget Order entity);

    // UPDATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "totalPrice", source = "totalPrice"),
            @Mapping(target = "status", source = "status")
    })
    void copyForUpdate(OrderRequest req, @MappingTarget Order entity);
}
