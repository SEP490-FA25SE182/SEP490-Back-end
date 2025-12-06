package com.sep.arservice.exception;

public record ErrorResponse(String code, String message, String details) {}
