package com.sep.arservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ARSceneItemRequest {
    @NotBlank
    @Size(max=50)
    String sceneId;

    @Size(max=50)
    String asset3DId;

    Integer orderIndex;

    float posX; float posY; float posZ;

    float rotX; float rotY; float rotZ;

    float scaleX; float scaleY; float scaleZ;

    @Size(max=2000)
    String behaviorJson;
}
