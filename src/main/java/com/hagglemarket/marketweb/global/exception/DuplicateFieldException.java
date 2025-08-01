package com.hagglemarket.marketweb.global.exception;

import java.util.Map;

//중복 필드 예외 클래스
public class DuplicateFieldException extends RuntimeException {
    //오류 담을 Map errors 객체 생성
    private final Map<String, String> errors;

    //생성자 : Map 형태의 오류 정보를 받아서 필드에 저장
    public DuplicateFieldException(Map<String, String> errors) {
        super("Duplicate field(s) found:");
        this.errors = errors;
    }

    //Getter : 나중에 ControllerAdvice에서 오류 Map을 꺼낼 때 사용
    public Map<String, String> getErrors() {
        return errors;
    }
}
