package com.zrl.rlmall.user.controller;

import com.zrl.rlmall.user.dto.LoginFormDTO;
import com.zrl.rlmall.user.dto.Result;
import com.zrl.rlmall.user.dto.UserDTO;
import com.zrl.rlmall.user.service.UserService;
import com.zrl.rlmall.user.utils.UserHolder;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserLoginController {

    @Autowired
    private UserService userService;

    /**
     *
     * 发送手机验证码
     * @param phone
     * @param session
     * @return
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("Phone") String phone, HttpSession session){

        return userService.sendCode(phone,session);
    }

    @PostMapping("login")
    public Result login(@RequestParam("Phone")LoginFormDTO loginForm, HttpSession session){

        return userService.login(loginForm,session);
    }

    @GetMapping("me")
    public Result me(){
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }
}
