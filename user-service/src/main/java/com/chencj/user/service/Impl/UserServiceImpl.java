package com.chencj.user.service.Impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.common.utils.Result;
import com.chencj.common.utils.UserContext;
import com.chencj.user.mapper.UserMapper;
import com.chencj.user.model.dto.UserDto;
import com.chencj.user.model.po.User;
import com.chencj.user.model.vo.UserVo;
import com.chencj.user.service.UserService;
import com.chencj.user.utils.JwtTool;
import com.chencj.user.utils.UserConstant;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @ClassName: UserServiceImpl
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 11:08
 * @Version: 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private JwtTool jwtTool;

    @Override
    public Result<?> login(UserDto userDto) {
        // 检索数据库有没有该用户
        User user = lambdaQuery()
                .eq(User::getUserAccount, userDto.getUserAccount())
                .one();
        if(user == null) {
            return Result.error("该用户不存在");
        }
        if(user.getIsDelete() == 1) {
            return Result.error(Result.UNAUTHORIZED, "该用户已注销");
        }
        if(!user.getUserPassword().equals(userDto.getUserPassword())) {
            return Result.error("密码输入错误");
        }

        String token = jwtTool.createToken(user.getId());
        return Result.ok(token, "登陆成功");
    }

    @Override
    public Result<?> register(UserDto userDto) {
        if(StrUtil.isBlank(userDto.getUserAccount()) || StrUtil.isBlank(userDto.getUserPassword())) {
            return Result.error("请输入完整的账号或者密码");
        }
        // 查询数据库有没有重复的用户名
        Long count = lambdaQuery().eq(User::getUserAccount, userDto.getUserAccount()).count();
        if(count != null && count > 0) {
            return Result.error(Result.CONFLICT, "该账号已存在，请重新输入");
        }

        // 不存在就封装用户信息
        User user = BeanUtil.copyProperties(userDto, User.class);
        user.setUserName(RandomUtil.randomString(16));
        user.setUserAvatar(UserConstant.USER_DEFAULT_AVATAR_PATH);
        user.setCreateTime(LocalDateTime.now());
        user.setUserRole(0);
        user.setIsDelete(0);
        boolean save = save(user);
        if(!save) return Result.error(Result.CONFLICT, "用户创建失败");
        return Result.ok().message("创建成功");
    }

    @Override
    public Result<?> getUserInfo() {
        Integer userId = UserContext.getUser();
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "获取用户信息失败，请重新登录");
        }
        User user = getById(userId);
        if(user == null) {
            return Result.error(Result.UNAUTHORIZED, "获取用户信息失败，请重新登录");
        }
        UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);
        return Result.ok(userVo);
    }
}
