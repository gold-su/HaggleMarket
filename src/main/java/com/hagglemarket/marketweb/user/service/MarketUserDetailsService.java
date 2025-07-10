package com.hagglemarket.marketweb.user.service;

import com.hagglemarket.marketweb.security.CustomUserDetails;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userid)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾지 못했습니다"));

        return new CustomUserDetails(user);
    }
}
