package com.hagglemarket.marketweb.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
//예외(오류) 정보를 담을 DTO
public class ErrorResponse {
    private int status;
    private String error;
    private Map<String, String> message;
    private LocalDateTime timestamp;
}
