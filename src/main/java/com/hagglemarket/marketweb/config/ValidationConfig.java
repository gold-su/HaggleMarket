package com.hagglemarket.marketweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.Validator;

@Configuration //스프링 설정 파일 지정 / 자동 빈 등록
public class ValidationConfig {
    //전역 Validator 빈 등록
    @Bean //이 메서드가 반환하는 객체를 스프링 컨테이너에 Bean으로 등록하여 다른 클래스들에서 주입 받아서 사용가능
    public Validator validatorFactory() {
        return new LocalValidatorFactoryBean(); //spring의 유효성 검사기를 만들어주는 팩토리 클래스, @NotBlank, @Size, @Pattern 등을 검증
    }
}
