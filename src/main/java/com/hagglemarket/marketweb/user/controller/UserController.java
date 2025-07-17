package com.hagglemarket.marketweb.user.controller;

import com.hagglemarket.marketweb.user.dto.*;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.service.UserService;
import com.hagglemarket.marketweb.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import java.util.Map;

@RestController//@Controller + @ResponseBody를 합친 어노테이션 / 반환값을 JSON 형식으로 자동 변환
@RequiredArgsConstructor //final 필드 자동으로 생성자 주입
@RequestMapping("/api/users") //모든 API는 /api/users로 시작
@Slf4j //log.info() 사용할 때 필요한 로그 자동 선언
public class UserController {
    //html할당
    private final UserService userService;
    private final JwtUtil jwtUtil;

    //서버 로컬 폴더
    private final String uploadDir = "uploads/";

    //비즈니스 로직을 담당할 UserService 주입
    //회원가입 기능 실제로 수행
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("login");

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

        log.info("login response: " + response); //로그인 응답 확인용
        return ResponseEntity.ok(response);
    }



//    //비즈니스 로직을 담당할 UserService 주입
//    //회원가입 기능 실제로 수행
//    @PostMapping("/signup") //POST 요청으로 엔드포인트 지정 / React에서 axios.post("/api/users/signup", {...}) 요청이 여기로 연결
//    public ResponseEntity<String> signUp(@RequestBody @Valid UserJoinDTO dto) {
//        //ResponseEntity<String>: 응답으로 문자열 메시지를 보내고, 상태 코드도 포함 가능
//        //@RequestBody: JSON 데이터를 UserJoinDTO 객체로 바인딩
//        //@Valid: DTO 클래스에 설정된 유효성 검사(예: @NotBlank,Size,Email)를 실행
//
//        //userService에 join 메서드 실행
//        userService.join(dto);
//
//        //회원가입 성공 body로 보내줌. 위에서 오류 발생시 throw로 인해 실행되지 않음
//        //.ok는 200번대 성공 HTTP 상태 코드
//        return ResponseEntity.ok("회원가입 성공");
//    }

    //비즈니스 로직을 담당할 UserService 주입
    //회원가입 기능 실제로 수행
    @PostMapping("/signup") //POST 요청으로 엔드포인트 지정 / React에서 axios.post("/api/users/signup", {...}) 요청이 여기로 연결
    public ResponseEntity<String> signUp(
            @RequestPart("user") UserJoinDTO dto,
            @RequestPart("profileImage") MultipartFile profileImage) {
        try {

            log.info("회원가입 요청 : {}", dto);
            log.info("업로된 이미지 이름 : {}", profileImage.getOriginalFilename());

            //이미지 파일 저장 로직
            String folderName = "profile";
            String fileName = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
            Path path = Paths.get(uploadDir, folderName,"/", fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, profileImage.getBytes());
            
            //폴더명과 파일명이 포함한 상태로 이미지 저장
            dto.setImageURL("http://localhost:8080/uploads/"+folderName+"/"+fileName);

            //userService에 join 메서드 실행
            userService.join(dto);

            //회원가입 성공 body로 보내줌. 위에서 오류 발생시 throw로 인해 실행되지 않음
            //.ok는 200번대 성공 HTTP 상태 코드
            return ResponseEntity.ok("회원가입 성공");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패");
        }
    }

    //api/users/me
    //현재 로그인한 사용자의 정보 조회
    //리턴 타입: ResponseEntity<UserInfoDTO>
    //클라이언트에게 HTTP 상태 코드 + 데이터(Json)를 함께 반환
    //파라미터: @RequestHeader("Authorization")  RequestHeader는 요청에서 헤더에 있는 데이터를 가져올 수 있는 방식 (token)
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

    //수정
    @PutMapping("/update")
    public ResponseEntity<LoginResponseDTO> updateUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody UserUpdateDTO updateDTO) {

        //JWT에서 userId 추출
        String userId = jwtUtil.extractUserId(token.replace("Bearer ",""));

        // DB에서 userId로 사용자를 찾아서 사용자 정보 수정 및 새 토큰 발급
        LoginResponseDTO response = userService.updateUserInfo(userId, updateDTO);

        // JSON 형태로 반환 (토큰 + 닉네임 포함)
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordChangeDTO dto) {

        //JWT에서 userId 추출
        String userId = jwtUtil.extractUserId(token.replace("Bearer ","").trim());

        //비밀번호 변경
        userService.changePassword(userId, dto.getCurrentPassword(), dto.getNewPassword());

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

//    //맵핑                                //consumes는 HTTP 요청의 Content-Type을 검사
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 컨트롤러 메서드가 multipart/form-data 형식의 요청만 처리하도록 강제 설정
//    //클라이언트가 보낸 이미지 파일을 받음
//    //@RequestParam("file"): 요청의 form-data에 포함된 "file" 파라미터로 파일 받기
//    //MultipartFile: 업로드된 파일 데이터가 들어있는 객체
//    public ResponseEntity<String> uploadProfileImage(@RequestPart("file") MultipartFile file) {
//        try{
//            //파일 저장 결로 수정
//            String folderName = "profile"; // 회원가입 프로필 이미지 폴더
//            //중복 방지를 위해 랜덤한 UUID로 파일 이름 생성
//            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//            //파일 저장 경로 생성
//            //uploadDir: 파일을 저장할 폴더 경로 예) "uploads/" 👉 실제 저장 위치는 uploads/랜덤파일명
//            Path path = Paths.get(uploadDir + folderName + "/" + fileName);
//            //uploads/ 폴더가 없으면 자동 생성
//            Files.createDirectories(path.getParent());
//            //업로드된 파일 데이터를 바이트 배열로 읽어서 서버에 저장
//            Files.write(path, file.getBytes());
//
//            //클라이언트가 나중에 이 파일에 접근할 수 있는 URL 생성
//            //React가 회원가입/마이페이지 화면에 이 URL을 넣어주면 됨
//            String fileUrl = "http://localhost:8080/uploads/" + folderName + "/" + fileName; //반환할 URL
//
//            return ResponseEntity.ok(fileUrl); //업로드 성공 시 HTTP 200 OK와 함께 파일 URL 반환
//        }catch (IOException e){
//            //HTTP 500 에러 반환
//            //"Upload failed"라는 메시지 전달
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
//        }
//    }

    //비밀번호 확인
    @PostMapping("/check-password")          //클라이언트가 JSON으로 보낸 데이터 payload에 담음 //요청 헤더에 있는 토큰을 가져오기 위해 사용
    public ResponseEntity<String> checkPassword(@RequestBody Map<String,String> payload, HttpServletRequest request){
        //클라이언트가 보낸 비밀번호 추출
        String password = payload.get("password");

        //토큰에서 사용자 ID 가져오기
        String token = request.getHeader("Authorization").replace("Bearer ","").trim(); //헤더에서 JWT 토큰 추출
        String userId = jwtUtil.extractUserId(token); //JWT에서 사용자 ID 추출

        //비밀번호 확인
        boolean isCorrect = userService.checkPassword(userId, password);
        if(isCorrect){
            return ResponseEntity.ok("비밀번호 일치");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 불일치");
        }

    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> withdrawUser(@PathVariable String userId,@RequestHeader("Authorization") String token) {
        String extractedUserId = jwtUtil.validateAndExtractUserId(token);

        log.info("요청 userId: " + userId);
        log.info("토큰에서 추출된 userId: " + extractedUserId);

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
