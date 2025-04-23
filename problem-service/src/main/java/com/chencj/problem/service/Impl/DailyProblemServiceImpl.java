package com.chencj.problem.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.common.utils.Result;
import com.chencj.problem.mapper.DailyProblemMapper;
import com.chencj.problem.model.po.DailyProblem;
import com.chencj.problem.model.vo.DailyProblemListVO;
import com.chencj.problem.service.DailyProblemService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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

    /**
     * 返回每一个月的每日一题信息
     * @param timeStamp
     * @return
     */
    @Override
    public Result<?> getProblemOfMonth(Long timeStamp) {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), zoneId);

        // 获取当月第一天和最后一天
        LocalDateTime firstDay = dateTime.with(TemporalAdjusters.firstDayOfMonth())
                .with(LocalTime.MIN); // 当月第一天 00:00:00
        LocalDateTime lastDay = dateTime.with(TemporalAdjusters.lastDayOfMonth())
                .with(LocalTime.MAX); // 当月最后一天 23:59:59.999

        // 题目集合
        List<DailyProblem> dailyProblemList = lambdaQuery().between(DailyProblem::getCreateTime, firstDay, lastDay).list();
        return Result.ok(new DailyProblemListVO(dailyProblemList));
    }
}
