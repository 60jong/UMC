package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.post.model.PatchPostsReq;
import com.example.demo.src.post.model.PostPostsReq;
import com.example.demo.src.post.model.PostPostsRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.POST_POSTS_INVALID_CONTENTS;
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
    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostsRes> createPost(@RequestBody PostPostsReq postPostsReq) throws BaseException {
        try{
            if(postPostsReq.getContent().length() > 450) {
                throw new BaseException(POST_POSTS_INVALID_CONTENTS);
            }
            if(postPostsReq.getPostImgUrls().size()<1) {
                throw new BaseException(POST_POSTS_EMPTY_IMGURL);
            }

            PostPostsRes postPostsRes = postService.createPosts(postPostsReq);
            return new BaseResponse<>(postPostsRes);
        } catch(BaseException exception){
            throw new BaseException(POST_POSTS_INVALID_CONTENTS);
        }
    }
    @ResponseBody
    @PatchMapping("/{postIdx}")
    public BaseResponse<String> modifyPost(@PathVariable("postIdx") int postIdx, @RequestBody PatchPostsReq patchPostsReq) throws BaseException {
        try{
            if(patchPostsReq.getContent().length() > 450) {
                throw new BaseException(POST_POSTS_INVALID_CONTENTS);
            }
            postService.modifyPost(patchPostsReq.getUserIdx(), postIdx, patchPostsReq);
            String result = "게시글 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            throw new BaseException(POST_POSTS_INVALID_CONTENTS);
        }
    }

    @ResponseBody
    @PatchMapping("/{postIdx}/status")
    public BaseResponse<String> modifyPostStatus(@PathVariable("postIdx") int postIdx) throws BaseException {
        postService.modifyPostStatus(postIdx);
        String result = "게시글 비활성화에 성공하였습니다.";
        return new BaseResponse<>(result);
    }

    @ResponseBody
    @DeleteMapping("/{postIdx}")
    public BaseResponse<String> deletePost(@PathVariable("postIdx") int postIdx) throws BaseException {
        try{
            postService.deletePost(postIdx);
            String result = "게시글 삭제를 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            throw new BaseException(POST_POSTS_INVALID_CONTENTS);
        }
    }
    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */

}
