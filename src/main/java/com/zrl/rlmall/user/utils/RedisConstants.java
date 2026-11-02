package com.zrl.rlmall.user.utils;

// Redis常量类
public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";

    public static final Long LOGIN_CODE_TTL = 2L;

    public static final String LOGIN_TOKEN = "login:token:";

    public static final Long LOGIN_USER_TTL = 30L;
}
