package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/user")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<GetUserRes> getUserByIdx(@PathVariable("userIdx") int userIdx) throws BaseException {
        if(userProvider.checkUser(userIdx) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try{
            GetUserRes getUsersRes = userProvider.getUserByIdx(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/{userIdx}/info") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<GetUserInfoRes> getUserInfoByIdx(@PathVariable("userIdx") int userIdx) throws BaseException {
        if(userProvider.checkUser(userIdx) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            GetUserInfoRes getUserInfoRes = userProvider.getUsersInfoByIdx(userIdx);
            return new BaseResponse<>(getUserInfoRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @GetMapping("/{userIdx}/posts")
    public BaseResponse<GetUserFeedsRes> getUserFeedsByIdx(@PathVariable("userIdx") int userIdx) throws BaseException {
        if(userProvider.checkUser(userIdx) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        GetUserFeedsRes getUserFeedsRes = userProvider.retrieveUserFeeds(userIdx,userIdx);

        return new BaseResponse<>(getUserFeedsRes);
    }
    /**
     * ???????????? API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:9000/users
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email ????????? ?????? validation ???????????????. ??? ??? ??? ??????????????? ??????????????????!
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // ????????? ????????????
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????????????????? API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user) throws BaseException {
        if(userProvider.checkUser(userIdx) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {

            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getNickName());
            userService.modifyUserName(patchUserReq);

            String result = "?????? ????????? ????????? ??????????????????.";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PatchMapping("/{userIdx}/status")
    public BaseResponse<DeleteUserRes> modifyUserStatus(@PathVariable("userIdx") int userIdx) throws BaseException {
        if(userProvider.checkUser(userIdx) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        DeleteUserRes deleteUserRes = userService.modifyUserStatus(userIdx);
        return new BaseResponse<>(deleteUserRes);
    }

}
