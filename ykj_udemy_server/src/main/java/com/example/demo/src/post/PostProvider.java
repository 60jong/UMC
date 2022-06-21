package com.example.demo.src.post;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.GetUserFeedsRes;
import com.example.demo.src.user.model.GetUserInfoRes;
import com.example.demo.src.user.model.GetUserPostsRes;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

//Provider : Read의 비즈니스 로직 처리
@Service
public class PostProvider {

    private final PostDao postDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }

    public GetUserRes getUserByIdx(int userIdx) throws BaseException{
        try{
            GetUserRes getUserRes = postDao.selectUserByIdx(userIdx);
            return getUserRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserInfoRes getUsersInfoByIdx(int userIdx) throws BaseException {
        try {
            GetUserInfoRes getUserInfoRes = postDao.selectUserInfoByIdx(userIdx);
            return getUserInfoRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
    public GetUserFeedsRes retrieveUserFeeds(int userJwt, int userIdx) {
        boolean _isMyFeed = true;
        if(userJwt != userIdx)
            _isMyFeed = false;

        GetUserInfoRes getUserInfoRes = postDao.selectUserInfoByIdx(userIdx);
        List<GetUserPostsRes> getUserPostsResList = postDao.selectUserPostsByIdx(userIdx);

        GetUserFeedsRes getUserFeedsRes = new GetUserFeedsRes(_isMyFeed, getUserInfoRes, getUserPostsResList);

        return getUserFeedsRes;
    }

    public int checkUser(int userIdx) {
        return postDao.checkUser(userIdx);
    }




    public int checkEmail(String email) throws BaseException{
        try{
            return postDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
