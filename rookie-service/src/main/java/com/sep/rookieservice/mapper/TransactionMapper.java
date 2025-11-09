package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.TransactionRequest;
import com.sep.rookieservice.dto.TransactionResponse;
import com.sep.rookieservice.entity.Transaction;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransactionMapper {

    TransactionResponse toResponse(Transaction entity);

    // CREATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "totalPrice", source = "totalPrice"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "orderCode", source = "orderCode"),
            @Mapping(target = "orderId", source = "orderId"),
            @Mapping(target = "paymentMethodId", source = "paymentMethodId"),
            @Mapping(target = "transType", source = "transType"),
            @Mapping(target = "walletId", source = "walletId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(TransactionRequest req, @MappingTarget Transaction entity);

    // UPDATE
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "totalPrice", source = "totalPrice"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "orderCode", source = "orderCode"),
            @Mapping(target = "transType", source = "transType"),
            @Mapping(target = "walletId", source = "walletId"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(TransactionRequest req, @MappingTarget Transaction entity);
}
