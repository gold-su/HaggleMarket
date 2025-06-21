package com.hagglemarket.marketweb.user.dao;

import com.hagglemarket.marketweb.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //로그인
    public User selectUser(User userVO) {
        System.out.println("로그인 데이터베이스 접근중");

        String sql = "select * from user where userId = ? and passWord = ?";

        List<User> userVOS = new ArrayList<>();

        try{
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            userVOS = jdbcTemplate.query(sql, rowMapper, userVO.getUserId(), userVO.getPassword());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return  userVOS.size() > 0 ? userVOS.get(0) : null;
    }

}
