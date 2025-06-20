package com.hagglemarket.marketweb.user.dto;

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

    public UserVO selectUser(UserVO userVO) {
        System.out.println("로그인 데이터베이스 접근중");

        String sql = "select * from user where userid = ? and password = ?";

        List<UserVO> userVOS = new ArrayList<UserVO>();

        try{
            RowMapper<UserVO> rowMapper = BeanPropertyRowMapper.newInstance(UserVO.class);
            userVOS = jdbcTemplate.query(sql, rowMapper, userVO.getUserid(), userVO.getPassword());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return  userVOS.size() > 0 ? userVOS.get(0) : null;
    }
}
