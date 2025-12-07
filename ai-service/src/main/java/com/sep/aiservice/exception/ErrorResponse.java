package com.sep.aiservice.exception;

public record ErrorResponse(String code, String message, String details) {}
