package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/post")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;




    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService){
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<List<GetPostsRes>> getPosts(@RequestParam("userIdx") int userIdx) throws BaseException {
        if(postProvider.checkUser(userIdx) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try{
            List<GetPostsRes> getPostsResList = postProvider.retrievePosts(userIdx,userIdx);
            return new BaseResponse<>(getPostsResList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @GetMapping("/{userIdx}/posts")
    public BaseResponse<GetUserFeedsRes> getUserFeedsByIdx(@PathVariable("userIdx") int userIdx) throws BaseException {
        if(postProvider.checkUser(userIdx) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        GetUserFeedsRes getUserFeedsRes = postProvider.retrieveUserFeeds(userIdx,userIdx);

        return new BaseResponse<>(getUserFeedsRes);
    }
    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body


    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */

}
