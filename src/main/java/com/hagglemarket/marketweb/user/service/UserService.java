package com.hagglemarket.marketweb.user.service;

import com.hagglemarket.marketweb.user.dto.UserJoinDTO;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service //비즈니스 로직 수행하는 클래스 지정
@RequiredArgsConstructor // final 필드 생성자 자동 생성
public class UserService {
    //final == 생성자에서만 초기화 가능, 불변
    private final UserRepository userRepository; //JPA 인터페이스
    private final PasswordEncoder passwordEncoder; //스프링 시큐리티 비밀번호 암호화 인터페이스

//    @Autowired
//    private UserDao userDao;


    //회원가입 처리
    //사용자가 회원가입할 때 보내준 데이터(UserJoinDTO)를 받아서
    //유효성 검증, DB에 저장
    public void join(UserJoinDTO dto) {
        if (userRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("userId:이미 존재하는 아이디입니다.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("email:이미 존재하는 이메일입니다.");
        }

        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("phoneNumber:이미 존재하는 전화번호입니다.");
        }

        if (userRepository.existsByNickName(dto.getNickName())) {
            throw new IllegalArgumentException("nickName:이미 존재하는 닉네임입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .userId(dto.getUserId())
                .password(encodedPassword)
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .nickName(dto.getNickName())
                .address(dto.getAddress())
                .imageURL(dto.getImageURL())
                .build();

        userRepository.save(user);
    }




//    //로그인 확인
//    public UserVO loginConfirm(UserVO userVO) {
//        System.out.println("로그인 확인중");
//
//        UserVO loginedUserVo = userDao.selectUser(userVO);
//
//        if (loginedUserVo == null) {
//            System.out.println("로그인 실패!!");
//        } else {
//            System.out.println("로그인 성공!!");
//        }
//
//        return loginedUserVo;
//    }
}
