package com.sep.arservice.mapper;

import com.sep.arservice.dto.*;
import com.sep.arservice.model.ARScene;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ARSceneMapper {

    ARSceneResponse toResponse(ARScene e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "markerId",  source = "markerId")
    @Mapping(target = "name",      source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "version",   source = "version")
    @Mapping(target = "status",    source = "status")
    @Mapping(target = "isActived",   source = "isActived")
    void copyForCreate(ARSceneRequest req, @MappingTarget ARScene e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name",      source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "version",   source = "version")
    @Mapping(target = "status",    source = "status")
    @Mapping(target = "isActived",   source = "isActived")
    void copyForUpdate(ARSceneRequest req, @MappingTarget ARScene e);

    default ARSceneWithItemsResponse compose(ARSceneResponse scene,
                                             MarkerResponse marker,
                                             java.util.List<Asset3DResponse> assets,
                                             java.util.List<ARSceneItemResponse> items) {
        ARSceneWithItemsResponse r = new ARSceneWithItemsResponse();
        r.setScene(scene);
        r.setMarker(marker);
        r.setAssets(assets);
        r.setItems(items);
        return r;
    }
}
