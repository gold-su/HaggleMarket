package com.hagglemarket.marketweb.security;

import com.hagglemarket.marketweb.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final int userNo;         // DB PK
    private final String username;    // 로그인 ID (ex. userId)
    private final String password;    // 암호화된 비밀번호

    public CustomUserDetails(User user) {
        this.userNo = user.getUserNo();        // User 엔티티 PK에 맞게!
        this.username = user.getUserId();    // 로그인 ID
        this.password = user.getPassword();  // 암호화된 비밀번호
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 안 쓰니까 빈 리스트 리턴!
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
