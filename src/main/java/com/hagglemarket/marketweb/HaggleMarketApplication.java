package com.hagglemarket.marketweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling //스프링에서 스케쥴러 어노테이션을 사용 가능하게 해줌
@SpringBootApplication
public class HaggleMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaggleMarketApplication.class, args);
    }


}
