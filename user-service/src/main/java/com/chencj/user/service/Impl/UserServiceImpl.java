package com.chencj.user.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.exception.UnauthorizedException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: UserServiceImpl
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 11:08
 * @Version: 1.0
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private JwtTool jwtTool;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${config.jwt.expire}")
    private Long duration_time;

    @Override
    public Result<?> login(UserDto userDto) {
        // 检索数据库有没有该用户
        User user = lambdaQuery()
                .eq(User::getUserAccount, userDto.getUserAccount())
                .one();
        if (user == null) {
            return Result.error("该用户不存在");
        }
        if (user.getIsDelete() == 1) {
            return Result.error(Result.UNAUTHORIZED, "该用户已注销");
        }
        if (!bCryptPasswordEncoder.matches(userDto.getUserPassword(), user.getUserPassword())) {
            return Result.error("密码输入错误");
        }

        String token = jwtTool.createToken(user.getId());
        return Result.ok(token, "登陆成功");
    }

    @Override
    public Result<?> register(UserDto userDto) {
        if (StrUtil.isBlank(userDto.getUserAccount()) || StrUtil.isBlank(userDto.getUserPassword())) {
            return Result.error("请输入完整的账号或者密码");
        }
        // 查询数据库有没有重复的用户名
        Long count = lambdaQuery().eq(User::getUserAccount, userDto.getUserAccount()).count();
        if (count != null && count > 0) {
            return Result.error(Result.CONFLICT, "该账号已存在，请重新输入");
        }

        // 不存在重复的账号就封装用户信息
        User user = BeanUtil.copyProperties(userDto, User.class);
        // 密码加盐值处理
        String saltPassword = bCryptPasswordEncoder.encode(user.getUserPassword());
        user.setUserPassword(saltPassword);
        user.setUserName(RandomUtil.randomString(16));
        user.setUserAvatar(UserConstant.USER_DEFAULT_AVATAR_PATH);
        user.setCreateTime(LocalDateTime.now());
        user.setUserRole(0);
        user.setIsDelete(0);
        boolean save = save(user);
        if (!save) return Result.error(Result.CONFLICT, "用户创建失败");
        return Result.ok().message("创建成功");
    }

    @Override
    public Result<?> getUserInfo() {
        Integer userId = UserContext.getUser();
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "获取用户信息失败，请重新登录");
        }
        // 先查询Redis中有没有用户相关信息，有的话直接拿，没有的话就从数据库拿
        String Key = RedisConstant.USER_INFO + userId;
        String user_info_str = stringRedisTemplate.opsForValue().get(Key);
        UserVo userVo = null;
        if (StrUtil.isBlank(user_info_str)) {
            User user = getById(userId);
            if (user == null) {
                return Result.error(Result.UNAUTHORIZED, "获取用户信息失败，请重新登录");
            }
            userVo = BeanUtil.copyProperties(user, UserVo.class);
            stringRedisTemplate.opsForValue().set(Key, JSONUtil.toJsonStr(userVo), duration_time, TimeUnit.SECONDS);
        } else {
            userVo = JSONUtil.toBean(user_info_str, UserVo.class);
        }

        return Result.ok(userVo);
    }

    @Override
    public Result<?> checkLogin(String token) {
        Integer res = null;
        try {
            res = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            log.error("error token {}", e.getMessage());
        }

        if(res == null) {
            return Result.error(401, "token失效");
        }
        return Result.ok();
    }
}
