package com.hagglemarket.marketweb.user.controller;

import com.hagglemarket.marketweb.user.dto.UserJoinDTO;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController //@Controller + @ResponseBody를 합친 어노테이션 / 반환값을 JSON 형식으로 자동 변환
@RequiredArgsConstructor //final 필드 자동으로 생성자 주입
@RequestMapping("/api/users") //모든 API는 /api/users로 시작
public class UserController {

    //비즈니스 로직을 담당할 UserService 주입
    //회원가입 기능 실제로 수행
    private final UserService userService;

//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/login")
//    public String login() {
//        System.out.println("로그인페이지 이동");
//
//        return "user/login";
//    }
//
//    @PostMapping("/login")
//    public String login(UserVO userVO, HttpSession session) {
//        System.out.println("로그인 시도 성공");
//
//        String nextPage = "user/success";
//        UserVO loginedUserVo = userService.loginConfirm(userVO);
//
//        if (loginedUserVo != null) {
//            session.setAttribute("loginedUser", loginedUserVo);
//            session.setMaxInactiveInterval(30 * 60);
//        }else {
//            nextPage = "user/fail";
//        }
//
//        return nextPage;
//    }




    @PostMapping("/signup") //POST 요청으로 엔드포인트 지정 / React에서 axios.post("/api/users/signup", {...}) 요청이 여기로 연결
    public ResponseEntity<String> signUp(@RequestBody @Valid UserJoinDTO dto) {
        //ResponseEntity<String>: 응답으로 문자열 메시지를 보내고, 상태 코드도 포함 가능
        //@RequestBody: JSON 데이터를 UserJoinDTO 객체로 바인딩
        //@Valid: DTO 클래스에 설정된 유효성 검사(예: @NotBlank,Size,Email)를 실행

        //userService에 join 메서드 실행
        userService.join(dto);

        //회원가입 성공 body로 보내줌. 위에서 오류 발생시 throw로 인해 실행되지 않음
        //.ok는 200번대 성공 HTTP 상태 코드
        return ResponseEntity.ok("회원가입 성공");
    }

}
