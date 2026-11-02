package com.zrl.rlmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zrl.rlmall.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

// 注册 Mapper，由 MP 生成用户表的基础 CRUD 实现。
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
