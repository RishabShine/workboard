package com.rishab.workboard.api.controller.errorHandling;

public record ErrorResponse(
        String error,
        String message
) {}
