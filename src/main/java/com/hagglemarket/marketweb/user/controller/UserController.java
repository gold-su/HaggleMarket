package com.hagglemarket.marketweb.user.controller;

import com.hagglemarket.marketweb.user.entity.UserVO;
import com.hagglemarket.marketweb.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller //로그인 확인
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        System.out.println("로그인페이지 이동");

        return "user/login";
    }

    @PostMapping("/login")
    public String login(UserVO userVO, HttpSession session) {
        System.out.println("로그인 시도 성공");

        String nextPage = "user/success";
        UserVO loginedUserVo = userService.loginConfirm(userVO);

        if (loginedUserVo != null) {
            session.setAttribute("loginedUser", loginedUserVo);
            session.setMaxInactiveInterval(30 * 60);
        }else {
            nextPage = "user/fail";
        }

        return nextPage;
    }
}
