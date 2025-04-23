package com.chencj.problem.service.Impl;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.utils.Result;
import com.chencj.common.utils.UserContext;
import com.chencj.problem.model.po.DailyProblem;
import com.chencj.problem.service.DailyProblemService;
import com.chencj.problem.service.SignInService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * @ClassName: SignInServiceImpl
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/22 14:54
 * @Version: 1.0
 */
@Slf4j
@Service
public class SignInServiceImpl implements SignInService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DailyProblemService dailyProblemService;

    /**
     * 每日一题签到
     * @param userId
     * @return
     */
    @Override
    public Result<?> signIn(Integer userId, Integer pid) {
        DailyProblem dailyProblem = dailyProblemService.lambdaQuery().eq(DailyProblem::getPid, pid).one();
        String Key = RedisConstant.USER_SIGNIN + userId + ":" + dailyProblem.getCreateTime().format(DateTimeFormatter.ofPattern("yyyyMM"));
        int dayOfMonth = dailyProblem.getCreateTime().getDayOfMonth() - 1;  // 位图从0开始
        Boolean isSigned = stringRedisTemplate.opsForValue().getBit(Key, dayOfMonth);
        if (BooleanUtil.isFalse(isSigned)) {
            // 还没签到
            stringRedisTemplate.opsForValue().setBit(Key, dayOfMonth, true);
        }

        return Result.ok();
    }

    @Override
    public Result<?> getUserSign(String date) {
        Integer userId = UserContext.getUser();
        String Key = RedisConstant.USER_SIGNIN + userId + ":" + date;
        JSONObject signResult = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();
        // 封装成yyyy-MM-dd : boolean
        stringBuilder.append(date, 0, 4);
        stringBuilder.append("-");
        stringBuilder.append(date, 4, 6);
        stringBuilder.append("-");
        for (int i = 0; i < 31; i++) {
            Boolean bit = stringRedisTemplate.opsForValue().getBit(Key, i);
            stringBuilder.append(String.format("%02d", i + 1));
            signResult.set(stringBuilder.toString(), bit);
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        // log.info("sign record : {}" , signResult);
        return Result.ok(signResult);
    }
}
