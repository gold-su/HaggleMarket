package com.hagglemarket.marketweb.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
//에러 메시지 여러 개 전송용 (입력 형식)
public class ErrorMapResponse {
    private int status;
    private String error;
    private Map<String, String> message;
    private LocalDateTime timestamp;
}
