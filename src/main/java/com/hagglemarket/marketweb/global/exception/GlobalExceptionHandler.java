package com.hagglemarket.marketweb.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice //@ControllerAdvice + @ResponseBody = @RestControllerAdvice / 모든 @RestController 에서 발생한 예외를 가로채서 처리할 수 있게 해줌
public class GlobalExceptionHandler {

    // 이 메서드는 IllegalArgumentException이 발생했을 때 호출됨
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e){
//        //응답 바디에는 예외 메시지 넣음 (e.getMessage() → "이미 존재하는 아이디입니다.")
//        return ResponseEntity.badRequest().body(e.getMessage());

        //ErrorResponse 클래스
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), //400
                "Bad Request",                  //에러 설명
                e.getMessage(),                 //예외 메시지
                LocalDateTime.now()             //현재 시간
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
