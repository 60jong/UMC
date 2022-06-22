package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthProvider authProvider;
    private final AuthService authService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(AuthProvider authProvider, AuthService authService, JwtService jwtService) {
        this.authProvider = authProvider;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) throws BaseException {
        if(postLoginReq.getEmail()==null)
            throw new BaseException(POST_USERS_EMPTY_EMAIL);
        if(postLoginReq.getPassword()==null)
            throw new BaseException(POST_USERS_EMPTY_PASSWORD);
        if(!isRegexEmail(postLoginReq.getEmail()))
            throw new BaseException(POST_USERS_INVALID_EMAIL);

        PostLoginRes postLoginRes = authService.logIn(postLoginReq);

        return new BaseResponse<PostLoginRes>(postLoginRes);
    }

    @ResponseBody
    @PostMapping("/test")
    public void test(@RequestParam("id") int id) throws BaseException {
        System.out.println(jwtService.getUserIdx());
    }
}
