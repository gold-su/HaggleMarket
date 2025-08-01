package com.hagglemarket.marketweb.user.service;

import com.hagglemarket.marketweb.global.exception.DuplicateFieldException;
import com.hagglemarket.marketweb.user.dto.LoginResponseDTO;
import com.hagglemarket.marketweb.user.dto.UserJoinDTO;
import com.hagglemarket.marketweb.user.dto.UserUpdateDTO;
import com.hagglemarket.marketweb.user.entity.User;

import com.hagglemarket.marketweb.user.entity.WithdrawUser;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import com.hagglemarket.marketweb.user.repository.WithdrawUserRepository;
import com.hagglemarket.marketweb.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service //비즈니스 로직 수행하는 클래스 지정
@RequiredArgsConstructor // final 필드 생성자 자동 생성
@Slf4j
public class UserService {
    //final == 생성자에서만 초기화 가능, 불변
    private final UserRepository userRepository; //JPA 인터페이스
    private final PasswordEncoder passwordEncoder; //스프링 시큐리티 비밀번호 암호화 인터페이스
    private final WithdrawUserRepository withdrawUserRepository;
    //@Autowired
    //private UserDao userDao;

    private final JwtUtil jwtUtil;

//    //회원가입 처리
//    //사용자가 회원가입할 때 보내준 데이터(UserJoinDTO)를 받아서
//    //유효성 검증, DB에 저장
    public void join(UserJoinDTO dto) {
        Map<String, String> errors = new HashMap<>();

        if (userRepository.existsByUserId(dto.getUserId())) {
            errors.put("userId","이미 존재하는 아이디입니다.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            errors.put("email","이미 존재하는 이메일입니다..");
        }

        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            errors.put("phoneNumber","이미 존재하는 전화번호입니다..");
        }

        if (userRepository.existsByNickName(dto.getNickName())) {
            errors.put("nickName","이미 존재하는 닉네임입니다.");
        }

        if(!errors.isEmpty()){
            throw new DuplicateFieldException(errors);
        }

        //비밀번호 평문을 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        //save 하기 편하게 회원가입 데이터를 user 엔티티로 변환
        // Entity로 변환 후 저장
        User user = User.builder()
                .userId(dto.getUserId())
                .password(dto.getPassword())
                .userName(dto.getUserName())
                .nickName(dto.getNickName())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .imageURL(dto.getImageURL())
                .build();

        //user 객체를 DB에 저장 (Insert 쿼리 발생)
        userRepository.save(user);
    }
//
//    //IllegalArgumentException 오류 Map 형태로 반환
//    public Map<String, String> vaildateAndJoin(UserJoinDTO dto){
//        Map<String, String> errors = new HashMap<>();
//
//        //DB 중복 체크
//        //레포지토리에 있는 existsByUserId 사용
//        if(userRepository.existsByUserId(dto.getUserId())){
//            errors.put("userId","이미 존재하는 아이디입니다.");
//        }
//        if(userRepository.existsByEmail(dto.getEmail())){
//            errors.put("email","이미 존재하는 이메일입니다.");
//        }
//        if(userRepository.existsByPhoneNumber(dto.getPhoneNumber())){
//            errors.put("userId","이미 존재하는 전화번호입니다.");
//        }
//        if(userRepository.existsByNickName(dto.getNickName())){
//            errors.put("nickName","이미 존재하는 닉네임입니다.");
//        }
//
//        //오류가 없으면 저장
//        if(errors.isEmpty()) {
//            //비밀번호 평문을 암호화
//            String encodedPassword = passwordEncoder.encode(dto.getPassword());
//            //save 하기 편하게 회원가입 데이터를 user 엔티티로 변환
//            User user = User.builder()
//                    .userId(dto.getUserId())
//                    .password(encodedPassword)
//                    .userName(dto.getUserName())
//                    .email(dto.getEmail())
//                    .phoneNumber(dto.getPhoneNumber())
//                    .nickName(dto.getNickName())
//                    .address(dto.getAddress())
//                    .imageURL(dto.getImageURL())
//                    .build();
//            //user 객체를 DB에 저장 (Insert 쿼리 발생)
//            userRepository.save(user);
//        }
//
//        return errors; //오류 Map 반환
//    }


    //로그인 로직
    public User login(String userId, String password){
        log.info("[UserService]");
        //데이터베이스에서 가져온 객체에 유저클래스 형식으로 저장함
        //만약 유저와 같은 값이 없다면 null값이 저장되어 날라옴
        Optional<User> userget = userRepository.findByUserId(userId);

        //만약 유저값이 비어있다면 예외처리
        if(userget.isEmpty()){
            throw new RuntimeException("존재하지 않는 사용자 입니다");
        }

        //받아온 유저값을 객체에 저장
        User user = userget.get();


        if(user.getStatus() == User.UserStatus.DELETED){
            throw new RuntimeException("탈퇴한 회원입니다");
        }
        //유저의 비밀번호가 일치하지않으면 오류
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        //예외처리가 안되었으면 유저정보를 반환
        return user;
    }

    //DB에서 userId로 사용자 조회하는 메서드
    public User findByUserId(String userId){
        return userRepository.findByUserId(userId) //DB에서 userId로 사용자 조회
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")); //Optional에 값이 있으면 -> 그 값을 반환 / 값이 없으면 RuntimeExceptopn 발생 '예외 메시지'
    }

    @Transactional //변화 감지 / 자동 세이브
    public LoginResponseDTO updateUserInfo(String userId, UserUpdateDTO dto) {
        //DB에서 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다!"));

        //사용자 정보 수정
        user.setEmail(dto.getEmail());
        user.setNickName(dto.getNickName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setImageURL(dto.getImageURL());

        //  수정 후 새 토큰 발급
        String newToken = jwtUtil.generateToken(user.getUserId());

        //  새 토큰과 새 닉네임 반환
        return new LoginResponseDTO(user.getUserId(), newToken, user.getNickName());

    }

    @Transactional //변화 감지 / 자동 세이브
    public void changePassword(String userId, String currentPassword, String newPassword) {
        // 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다!"));
        // 현재 비밀번호 확인
        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(newPassword));
    }


    //비밀번호 확인 로직
    public boolean checkPassword(String userId, String password) {
        //UserId로 DB에서 사용자 정보 조회
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if(optionalUser.isEmpty()){
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();

        //입력된 비밀번호와 DB의 암호화된 비밀번호 비교
        return passwordEncoder.matches(password, user.getPassword());
    }

    //탈퇴 로직
    public void withdraw(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);

        WithdrawUser withdrawUser = WithdrawUser.builder()
                .userId(userId)
                .userEmail(user.getEmail())
                .withdrawAt(LocalDateTime.now())
                .build();
        withdrawUserRepository.save(withdrawUser);
    }
}
