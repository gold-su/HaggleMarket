package com.hagglemarket.marketweb.user.service;

import com.hagglemarket.marketweb.user.dto.UserJoinDTO;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.dao.UserDao;
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
    public void join(UserJoinDTO dto){

        //중복 체크 / true 라면 중복
        if(userRepository.existsByUserId(dto.getUserId())){
            //throw는 예외 상황이 생기면 알리는 키워드
            //IllegalArgumentException는 잘못된 입력 값이 메서드에 전달되었음. 이라는 예외 클래스
            //스프링이 자동으로 400 Bad Request로 응답해줌
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if(userRepository.existsByEmail(dto.getEmail())){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if(userRepository.existsByPhoneNumber(dto.getPhoneNumber())){
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다.");
        }

        if(userRepository.existsByNickName(dto.getNickName())){
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        //비밀번호 평문을 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        //save 하기 편하게 회원가입 데이터를 user 엔티티로 변환
        User user = User.builder()
                .userId(dto.getUserId())
                .password(encodedPassword)
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .nickName(dto.getNickName())
                .address(dto.getAddress())
                .imageURL(dto.getImageURL())
                .build();

        //user 객체를 DB에 저장 (Insert 쿼리 발생)
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
