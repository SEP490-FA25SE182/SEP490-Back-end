package com.sep.rookieservice.mapper;

import com.sep.rookieservice.dto.WalletRequest;
import com.sep.rookieservice.dto.WalletResponse;
import com.sep.rookieservice.entity.Wallet;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WalletMapper {

    // Entity -> Response
    WalletResponse toResponse(Wallet entity);

    // Request -> Entity (CREATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "coin", source = "coin"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForCreate(WalletRequest req, @MappingTarget Wallet entity);

    // Request -> Entity (UPDATE)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "coin", source = "coin"),
            @Mapping(target = "isActived", source = "isActived")
    })
    void copyForUpdate(WalletRequest req, @MappingTarget Wallet entity);
}
