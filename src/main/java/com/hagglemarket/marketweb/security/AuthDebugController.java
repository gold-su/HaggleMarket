package com.hagglemarket.marketweb.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/debug")
public class AuthDebugController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication a) {
        return Map.of(
                "name", a == null ? null : a.getName(),
                "authorities", a == null ? List.of() : a.getAuthorities()
        );
    }
}