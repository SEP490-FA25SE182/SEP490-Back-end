package com.sep.rookieservice.dto;

import lombok.Data;

@Data
public class GhnBaseResponse<T> {
    private int code;
    private String message;
    private T data;
}
