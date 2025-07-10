package com.hagglemarket.marketweb.user.controller;

import com.hagglemarket.marketweb.user.dto.LoginRequestDTO;
import com.hagglemarket.marketweb.user.dto.LoginResponseDTO;
import com.hagglemarket.marketweb.user.dto.UserJoinDTO;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.service.UserService;
import com.hagglemarket.marketweb.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController//@Controller + @ResponseBody를 합친 어노테이션 / 반환값을 JSON 형식으로 자동 변환
@RequiredArgsConstructor //final 필드 자동으로 생성자 주입
@RequestMapping("/api/users") //모든 API는 /api/users로 시작
public class UserController {
    //html할당
    private final UserService userService;
    private final JwtUtil jwtUtil;

    //비즈니스 로직을 담당할 UserService 주입
    //회원가입 기능 실제로 수행
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        System.out.println("login");

        User user = userService.login(loginRequestDTO.getUserId(), loginRequestDTO.getPassword());

        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(user.getUserId());
        LoginResponseDTO response = new LoginResponseDTO(token, user.getUserId());

        return ResponseEntity.ok(response);
    }

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

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> withdrawUser(@PathVariable String userId,@RequestHeader("Authorization") String token) {
        String extractedUserId = jwtUtil.validateAndExtractUserId(token);

        System.out.println("요청 userId: " + userId);
        System.out.println("토큰에서 추출된 userId: " + extractedUserId);

        if(!userId.equals(extractedUserId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인만 탈퇴할수 있습니다.");
        }

        userService.withdraw(userId);
        return ResponseEntity.ok("회원 탈퇴 처리 완료");
    }

    @GetMapping("/nickname")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        String userId = jwtUtil.validateAndExtractUserId(token);

        //유저의 아이디를 객체에 담음
        User user = userService.findByUserId(userId);
        //해당객체를 사용하여 닉네임을 찾아옴
        Map<String,String> res = Map.of("nickName",user.getNickName());
        return ResponseEntity.ok(res);
    }
}
