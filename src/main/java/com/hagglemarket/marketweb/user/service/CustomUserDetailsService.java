package com.hagglemarket.marketweb.user.service;

import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
//InMemoryUserDetailsManager로 메모리 말고 DB에서 정보를 찾아라
public class CustomUserDetailsService implements UserDetailsService {

    //필드 선언 / DB에 접근할 수 있는 JPA 레포지토리를 주입
    //이 레포지토리로 DB에서 사용자 정보를 검색할 것
    private final UserRepository userRepository;

    //userRepository를 생성자 주입 받음
    //이 클래스가 생성될 떄 Spring이 UserRepository 인스턴스를 넣어줌
    //@Service로 등록되면 자동 주입 가능
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override //부모 인터페이스의 메서드를 재정의하고 있다는 표시 / 내부적으로 로그인 처리 시 이 메서드를 자동 호출함.
    //Spring Security가 로그인 시 호출하는 메서드, 매개변수 : userId / 반환값 : UserDetails (Spring Security가 인증 처리할 때 필요한 표준 사용자 객체)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException{
        //DB에서 사용자 찾기
        User user = userRepository.findByUserId(userId) //DB에서 userId로 사용자 찾기, 반환 타입: Optional<User>
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 :"+ userId)); //사용자가 존재하면 → user 반환 / 없으면 → UsernameNotFoundException 발생

        //Spring Security가 이해할 수 있는 UserDetails 객체로 변환
        return org.springframework.security.core.userdetails.User.builder() //Spring Security 전용 User 객체 생성
                .username(user.getUserId()) //DB에서 가져온 사용자 아이디 지정
                .password(user.getPassword()) //DB에 저장된 비밀번호 지정 (반드시 암호화된 상태여야 함)
                .roles("USER") //사용자 권한 지정 → 이후 Security에서 “이 사용자는 USER 권한이 있음”으로 처리
                .build(); //UserDetails 객체 완성해서 반환
    }
}
