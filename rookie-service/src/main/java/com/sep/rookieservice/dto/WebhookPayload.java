package com.sep.rookieservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class WebhookPayload {
    private String code;
    private String desc;
    private boolean success;
    private Map<String,Object> data;
    private String signature;
}
