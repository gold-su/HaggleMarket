package com.hagglemarket.marketweb.user.controller;

import com.hagglemarket.marketweb.user.dto.LoginRequestDTO;
import com.hagglemarket.marketweb.user.dto.LoginResponseDTO;
import com.hagglemarket.marketweb.user.dto.UserInfoDTO;
import com.hagglemarket.marketweb.user.dto.UserJoinDTO;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.service.UserService;
import com.hagglemarket.marketweb.user.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController//@Controller + @ResponseBody를 합친 어노테이션 / 반환값을 JSON 형식으로 자동 변환
@RequiredArgsConstructor //final 필드 자동으로 생성자 주입
@RequestMapping("/api/users") //모든 API는 /api/users로 시작
public class UserController {
    //html할당
    private final UserService userService;
    private final JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        System.out.println("login");

        User user = userService.login(loginRequestDTO.getUserId(), loginRequestDTO.getPassword());

        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(user.getUserId());
        LoginResponseDTO response = new LoginResponseDTO(
                user.getUserId(),
                token,
                user.getNickName()
        );

        System.out.println("login response: " + response); //로그인 응답 확인용
        return ResponseEntity.ok(response);
    }



    //비즈니스 로직을 담당할 UserService 주입
    //회원가입 기능 실제로 수행
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

    //api/users/me
    //현재 로그인한 사용자의 정보 조회
    //리턴 타입: ResponseEntity<UserInfoDTO>
    //클라이언트에게 HTTP 상태 코드 + 데이터(Json)를 함께 반환
    //파라미터: @RequestHeader("Authorization")
    //클라이언트가 보내는 JWT 토큰을 헤더에서 꺼내옴
    //예) Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getMyInfo(
            @RequestHeader("Authorization") String token) {

        // JWT 토큰에서 userId 추출
        //jwtUtil.extractUserId()를 통해 JWT 디코딩해서 사용자 ID 꺼냄
        String userId = jwtUtil.extractUserId(token.replace("Bearer","")); //"Bearer " 접두어를 제거 → 실제 토큰만 남김

        //DB에서 해당 userId를 가진 사용자 정보 조회, 없으면 예외 던지거나 오류 처리도 가능
        User user = userService.findByUserId(userId);

        //User 엔티티 -> UserInfoDTO 변환
        UserInfoDTO dto = new UserInfoDTO(
                user.getUserId(),
                user.getUserName(),
                user.getNickName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getImageURL()
        );

        //클라이언트에 DTO 반환
        return ResponseEntity.ok(dto);
    }
}
