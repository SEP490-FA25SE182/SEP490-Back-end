package com.sep.rookieservice.exception;

public record ErrorResponse(String code, String message, String details) {}
