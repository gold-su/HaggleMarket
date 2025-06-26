package com.hagglemarket.marketweb.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice //@ControllerAdvice + @ResponseBody = @RestControllerAdvice / 모든 @RestController 에서 발생한 예외를 가로채서 처리할 수 있게 해줌
public class GlobalExceptionHandler {

    // 이 메서드는 IllegalArgumentException이 발생했을 때 호출됨
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex){

        String message = ex.getMessage();
        Map<String, String> errorMap;

        // 예: "userId:이미 존재하는 아이디입니다."
        if (message.contains(":")) {
            String[] parts = message.split(":", 2);
            errorMap = Map.of(parts[0], parts[1]);
        } else {
            errorMap = Map.of("global", message);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), //400
                "Bad Request",                  //에러 설명
                errorMap,                       //예외 메시지
                LocalDateTime.now()             //현재 시간
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    //@Valid 검증 실패 시 (DTO 유효성 검사 실패 시)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex){
        Map<String, String> errorMap = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (existing, replacement) -> existing //중복 필드는 첫 번째 값 유지
                        )
                );
        ErrorResponse errorMapResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "validation error",
                errorMap,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(errorMapResponse);
    }
}
