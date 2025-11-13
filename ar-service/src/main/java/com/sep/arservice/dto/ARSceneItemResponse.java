package com.sep.arservice.dto;

import lombok.Data;

@Data
public class ARSceneItemResponse {
    String itemId;
    String sceneId;
    String asset3DId;
    Integer orderIndex;
    float posX; float posY; float posZ;
    float rotX; float rotY; float rotZ;
    float scaleX; float scaleY; float scaleZ;
    String behaviorJson;
}
