package com.chencj.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chencj.user.model.po.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}