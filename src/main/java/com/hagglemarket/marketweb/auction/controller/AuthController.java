package com.hagglemarket.marketweb.auction.controller;


import com.hagglemarket.marketweb.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> me(@org.springframework.security.core.annotation.AuthenticationPrincipal CustomUserDetails user,
                                  Authentication auth) {
        Map<String, Object> m = new HashMap<>();
        m.put("authenticated", auth != null && auth.isAuthenticated());
        m.put("principalClass", user == null ? null : user.getClass().getName());
        if (user != null) {
            m.put("userNo", user.getUserNo());
            m.put("username", user.getUsername());
        }
        return m;
    }
}