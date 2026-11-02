package com.zrl.rlmall.user.service;

import com.zrl.rlmall.user.dto.LoginFormDTO;
import com.zrl.rlmall.user.dto.Result;
import com.baomidou.mybatisplus.spring.service.IService;
import com.zrl.rlmall.user.entity.User;
import jakarta.servlet.http.HttpSession;


// 对外提供 MP 的通用用户 CRUD 方法。
public interface UserService extends IService<User> {
    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);
}
