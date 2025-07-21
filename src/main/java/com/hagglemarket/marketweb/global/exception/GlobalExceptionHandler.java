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
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex){

        String message = ex.getMessage();
        Map<String, String> errorMap;

        // 예: "userId:이미 존재하는 아이디입니다."
        if (message.contains(":")) { //예외 메시지에 ':'이 포함되어 있는지 확인 / 이유 : 서비스에서 예외를 던질 때 key:value 형태로 던지기 때문
            String[] parts = message.split(":", 2); // : 를 기준으로 문자열을 2조각으로 나눔
            errorMap = Map.of(parts[0], parts[1]); //나눈 key, value를 Map으로 묶음
        } else {
            errorMap = Map.of("global", message);
        }

        return ResponseEntity.badRequest().body(errorMap);
    }

    //@Valid 검증 실패 시 (DTO 유효성 검사 실패 시)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex){
        Map<String, String> errorMap = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing //중복 필드는 첫 번째 값 유지
                        )
                );


        return ResponseEntity.badRequest().body(errorMap);
    }
}
