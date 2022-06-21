package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfoRes {
    private int userIdx;
    private String name;
    private String nickName;
    private String birth;
    private String gender;
    private String website;
    private String profileImgUrl;
    private String introduction;
    private String email;
    private int followerCount;
    private int followingCount;
    private int postCount;
}
