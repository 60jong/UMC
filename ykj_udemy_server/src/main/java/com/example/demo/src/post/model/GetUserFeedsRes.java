package com.example.demo.src.post.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedsRes {
    private boolean _isMyFeed;
    private GetUserInfoRes getUserInfoResList;
    private List<GetUserPostsRes> getUserPostsResList;
}
