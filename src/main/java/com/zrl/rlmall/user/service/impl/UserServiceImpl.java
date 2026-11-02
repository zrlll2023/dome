package com.zrl.rlmall.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.zrl.rlmall.user.dto.LoginFormDTO;
import com.zrl.rlmall.user.dto.Result;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zrl.rlmall.user.dto.UserDTO;
import com.zrl.rlmall.user.entity.User;
import com.zrl.rlmall.user.mapper.UserMapper;
import com.zrl.rlmall.user.service.UserService;
import com.zrl.rlmall.user.utils.RegexUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.RandomUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zrl.rlmall.user.utils.RedisConstants.*;
import static com.zrl.rlmall.user.utils.SystemConstants.USER_NICK_NAME_PREFIX;

@Slf4j
@Service
// 继承 MP 的通用实现，复用用户表的查询和保存方法。
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1:校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2：如果不行就直接返回
            return Result.fail("手机号格式有误");
        }
        // 3：如果符合就生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 4：发送验证码到session
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code,LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 5：模拟发送验证码
        log.debug("发送短信验证码成功，验证码:{}", code);
        // 返回
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 校验手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式有误");
        }
        // 从redis获取验证码并校验
        String cacheCode = loginForm.getCode();
        String sessionCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        if (sessionCode == null || !cacheCode.equals(sessionCode)) {
            return Result.fail("验证码错误或者不存在");
        }

        User user = query()
                .eq("phone", phone)
                .one();

        if (user == null) {
            user = createUserWithPhone(phone);
        }

        // 保存用户信息到redis
        String token = UUID.randomUUID().toString(true);
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

        String tokenKey = LOGIN_TOKEN + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        // 设置token有效期
        stringRedisTemplate.expire(tokenKey,LOGIN_USER_TTL, TimeUnit.MINUTES);
        return Result.ok(token);
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomNumbers(8));
        save(user);
        return user;
    }
}
