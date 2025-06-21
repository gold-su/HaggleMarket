package com.hagglemarket.marketweb.user.repository;

//user entity를 다룰 import
import com.hagglemarket.marketweb.user.entity.User;
//JPA REPOSITORY 인터페이스
import org.springframework.data.jpa.repository.JpaRepository;
//NULL 안정성 클래스
import java.util.Optional;

//DB에 접근해서 데이터 조회/저장/삭제하는 역할
//DAO 대신 역할 / DAO는 JDBC << 구식
//interface로 정의
public interface UserRepository extends JpaRepository<User, Integer> { //User는 entity 클래스, Integer는 Id 값이 integer 라서

    //UserId가 DB에 이미 존재하는지 true/false로 확인
    //SELECT COUNT(*) > 0 FROM user WHERE user_id = ? << 자동 생성
    boolean existsByUserId(String userId);

    //email이 이미 존재하는지 확인
    //SELECT COUNT(*) > 0 FROM user WHERE email = ?
    boolean existsByEmail(String email);

    //phoneNumber가 이미 존재하는지 확인
    //SELECT COUNT(*) > 0 FROM user WHERE phoneNumber = ?
    boolean existsByPhoneNumber(String phoneNumber);

    //nickName이 이미 존재하는지 확인
    //SELECT COUNT(*) > 0 FROM user WHERE nickName = ?
    boolean existsByNickName(String nickName);

    //null이 나올수도 아닐수도 있기 때문에 Optional로 구현
    //userId를 기준으로 DB에서 찾는 메서드
    //로그인, 유저 정보 수정/조회, 인증/인가 처리할 때 사용예정
    Optional<User> findByUserId(String userId);
}
