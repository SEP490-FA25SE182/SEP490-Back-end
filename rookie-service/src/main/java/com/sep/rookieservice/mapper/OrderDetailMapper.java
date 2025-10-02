package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.OrderDetailRequest;
import com.sep.rookieservice.dto.OrderDetailResponse;
import com.sep.rookieservice.entity.OrderDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderDetailMapper {

    OrderDetailResponse toResponse(OrderDetail entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "price", source = "price")
    })
    void copyForCreate(OrderDetailRequest req, @MappingTarget OrderDetail entity);

    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "price", source = "price")
    })
    void copyForUpdate(OrderDetailRequest req, @MappingTarget OrderDetail entity);
}
