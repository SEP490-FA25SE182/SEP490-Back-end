package com.sep.arservice.dto;

import lombok.Data;

@Data
public class MeshyCreateReq {
    String mode = "preview";
    String prompt;
    String negative_prompt;
    String topology = "triangle";
    String format = "glb";
    String quality = "balanced";
}

