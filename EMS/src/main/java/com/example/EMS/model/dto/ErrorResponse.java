package com.example.EMS.model.dto;

import java.time.LocalDateTime;

public record ErrorResponse(int statusCode, String errorMessage, LocalDateTime timeStamp) {
}
