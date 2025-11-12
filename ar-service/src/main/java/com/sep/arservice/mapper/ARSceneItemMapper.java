package com.sep.arservice.mapper;

import com.sep.arservice.dto.ARSceneItemRequest;
import com.sep.arservice.dto.ARSceneItemResponse;
import com.sep.arservice.model.ARSceneItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ARSceneItemMapper {

    ARSceneItemResponse toResponse(ARSceneItem e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "sceneId",   source = "sceneId")
    @Mapping(target = "asset3dId", source = "asset3dId")
    @Mapping(target = "orderIndex",source = "orderIndex")
    @Mapping(target = "posX",      source = "posX")
    @Mapping(target = "posY",      source = "posY")
    @Mapping(target = "posZ",      source = "posZ")
    @Mapping(target = "rotX",      source = "rotX")
    @Mapping(target = "rotY",      source = "rotY")
    @Mapping(target = "rotZ",      source = "rotZ")
    @Mapping(target = "scaleX",    source = "scaleX")
    @Mapping(target = "scaleY",    source = "scaleY")
    @Mapping(target = "scaleZ",    source = "scaleZ")
    @Mapping(target = "behaviorJson", source = "behaviorJson")
    void copyForCreate(ARSceneItemRequest req, @MappingTarget ARSceneItem e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "orderIndex",source = "orderIndex")
    @Mapping(target = "posX",      source = "posX")
    @Mapping(target = "posY",      source = "posY")
    @Mapping(target = "posZ",      source = "posZ")
    @Mapping(target = "rotX",      source = "rotX")
    @Mapping(target = "rotY",      source = "rotY")
    @Mapping(target = "rotZ",      source = "rotZ")
    @Mapping(target = "scaleX",    source = "scaleX")
    @Mapping(target = "scaleY",    source = "scaleY")
    @Mapping(target = "scaleZ",    source = "scaleZ")
    @Mapping(target = "behaviorJson", source = "behaviorJson")
    void copyForUpdate(ARSceneItemRequest req, @MappingTarget ARSceneItem e);
}
