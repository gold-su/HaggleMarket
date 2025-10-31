package com.hagglemarket.marketweb.ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController  //REST API / return 값이 JSON 으로 자동 직렬화됨
@RequestMapping("/api/ai") //경로 설정
@RequiredArgsConstructor // final 필드 자동 생성자
public class AiChatController {

    //OpenAPI 호출용 객체 생성
    //RestTemplate는 Spring이 제공하는 HTTP 클라이언트
    //보통 @Bean 등록하고 @Autowired로 주입하지만 테스트용으로 직접 인스턴스 생성
    private final RestTemplate restTemplate = new RestTemplate();

    //OpenAI 인증키
    @Value("${openai.api-key}")
    private String OPENAI_API_KEY;

    //요청 본문 JSON 으로 {"message" : "질문내용"} 형태
    //Map<String, String> 는 JSON 형태로 응답을 반환
    @PostMapping("/faq")
    public ResponseEntity<Map<String, String>> faq(@RequestBody Map<String, String> req){
        //클라이언트가 보낸 JSON 에서 "message" 키의 값을 꺼냄
        String question = req.get("message");
        //질문이 비어 있으면 (null or empty) -> HTTP 상태 400 and "무엇이든 물어보세요!" 메시지 반환.
        if(question == null || question.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("answer", "무엇이든 물어보세요!"));
        }

        //유효한 질문이라면 callOpenAI() 메서드 호출 -> 실제로 OpenAI 서버에 요청
        //결과 result는 GPT의 응답 문자열
        String result = callOpenAi(question);

        //응답을 JSON 으로 감싸서 반환
        //{"answer" : "환불 절차는 ..."}
        return ResponseEntity.ok(Map.of("answer", result));
    }

    //사용자가 보낸 질문 question 으로 GPT API 호출을 실제로 수행하는 메서드
    private String callOpenAi(String question){
        //chat api 공식 엔드포인트
        String url = "https://api.openai.com/v1/chat/completions";
        //요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        //Authorization : API 키 인증용
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        //Content-Type : 요청 본문이 JSON 형식임을 명시
        headers.set("Content-Type", "application/json");

        //model : 사용할 모델 이름(gpt-4o-mini)
        //messages : 대화 히스토리 "system" : ai 역할 지시 / "user" : 사용자의 실제 질문
        //temperature : 창의성 조절 (0~1 사이, 높을수록 자유롭게 답변
        Map<String, Object> body = Map.of(
                "model","gpt-4o-mini",
                "messages", List.of(Map.of("role","system","content","너는 중고거래 플랫폼 '해글마켓'의 고객지원 챗봇이야. " + " 안전거래, 사기예방, 환불절차, 이용가이드 등에 대해 간단하고 친절하게 답변해."),
                        Map.of("role","user","content",question)
                ),
                "temperature", 0.7
        );

        //위의 body + headers를 합쳐서 하나의 HTTP 요청 엔티티로 만듦.
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try{
            //실제 HTTP POST 요청 수행.
            //URL : OPENAI API
            //ENTITY : BODY + HEADERS
            //응답은 JSON(Map 형태)로 받음.
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            //응답 구조 지정
            //{
            //  "choices": [
            //    {
            //      "message": {
            //        "role": "assistant",
            //        "content": "안녕하세요! 환불 절차는 다음과 같습니다..."
            //      }
            //    }
            //  ]
            //}
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return message.get("content").toString().trim();
        }catch(Exception e){
            //콘솔 에러 및 문구 반환.
            e.printStackTrace();
            return "AI 서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }
}
