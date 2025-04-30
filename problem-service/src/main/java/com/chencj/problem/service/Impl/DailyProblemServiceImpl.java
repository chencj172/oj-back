package com.chencj.problem.service.Impl;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.utils.Result;
import com.chencj.problem.mapper.DailyProblemMapper;
import com.chencj.problem.model.po.DailyProblem;
import com.chencj.problem.model.vo.DailyProblemListVO;
import com.chencj.problem.service.DailyProblemService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * @ClassName: DailyProblemServiceImpl
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/22 16:19
 * @Version: 1.0
 */
@Service
public class DailyProblemServiceImpl extends ServiceImpl<DailyProblemMapper, DailyProblem> implements DailyProblemService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 返回每一个月的每日一题信息
     * @param timeStamp
     * @return
     */
    @Override
    public Result<?> getProblemOfMonth(Long timeStamp) {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), zoneId);
        String Key = RedisConstant.DAILY_PROBLEM_OF_MONTH + dateTime.format(DateTimeFormatter.ofPattern("yyyyMM"));
        List<DailyProblem> dailyProblemList;
        if (stringRedisTemplate.hasKey(Key)) {
            dailyProblemList = stringRedisTemplate.opsForList().range(Key, 0, -1).stream().map((dailyProblemJson) -> JSONUtil.toBean(dailyProblemJson, DailyProblem.class)).toList();
            return Result.ok(new DailyProblemListVO(dailyProblemList));
        } else {
            // 获取当月第一天和最后一天
            LocalDateTime firstDay = dateTime.with(TemporalAdjusters.firstDayOfMonth())
                    .with(LocalTime.MIN); // 当月第一天 00:00:00
            LocalDateTime lastDay = dateTime.with(TemporalAdjusters.lastDayOfMonth())
                    .with(LocalTime.MAX); // 当月最后一天 23:59:59

            // 题目集合
            dailyProblemList = lambdaQuery().between(DailyProblem::getCreateTime, firstDay, lastDay).list();

            if (!dailyProblemList.isEmpty()) {
                List<String> dailyProblemJsonList = dailyProblemList.stream().map(JSONUtil::toJsonStr).toList();
                // 放到Redis中
                stringRedisTemplate.opsForList().rightPushAll(Key, dailyProblemJsonList);
            }
        }

        return Result.ok(new DailyProblemListVO(dailyProblemList));
    }
}
