package com.hagglemarket.marketweb.user.service;

import com.hagglemarket.marketweb.user.entity.UserVO;
import com.hagglemarket.marketweb.user.dto.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public UserVO loginConfirm(UserVO userVO) {
        System.out.println("로그인 확인중");

        UserVO loginedUserVo = userDao.selectUser(userVO);

        if (loginedUserVo == null) {
            System.out.println("로그인 실패!!");
        } else {
            System.out.println("로그인 성공!!");
        }

        return loginedUserVo;
    }
}
