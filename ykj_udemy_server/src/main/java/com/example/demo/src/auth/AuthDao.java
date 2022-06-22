package com.example.demo.src.auth;


import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class AuthDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public User getUser(PostLoginReq postLoginReq) {
        String getUserQuery = "select userIdx, name, nickName, email, password from user where email = ?";
        String getUserParam = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(
                getUserQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getString("password")),
                getUserParam);
    }
}
