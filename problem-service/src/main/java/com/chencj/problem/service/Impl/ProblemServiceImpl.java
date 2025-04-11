package com.chencj.problem.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.utils.Result;
import com.chencj.problem.mapper.ProblemMapper;
import com.chencj.problem.model.dto.ProblemCodeDto;
import com.chencj.problem.model.po.Problem;
import com.chencj.problem.model.vo.ProblemVo;
import com.chencj.problem.publisher.CodePublisher;
import com.chencj.problem.service.ProblemService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ProblemService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/9 18:55
 * @Version: 1.0
 */
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {

    @Resource
    private ProblemMapper problemMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CodePublisher codePublisher;

    @Override
    public Result<?> search(Integer level, String word, Integer pageNum, Integer pageSize) {
        Page<ProblemVo>page = new Page<>(pageNum, pageSize);
        problemMapper.search(page, level, word);
        return Result.ok(page);
    }

    @Override
    public Result<?> getProblemById(Integer id) {
        String Key = RedisConstant.PROBLEM_INFO_DETAIL + id;
        String problemJsonStr = stringRedisTemplate.opsForValue().get(Key);
        Problem problem = null;
        if (StrUtil.isBlank(problemJsonStr)) {
            // 查询数据库，更新redis
            problem = getById(id);
            stringRedisTemplate.opsForValue().set(Key, JSONUtil.toJsonStr(problem), 1, TimeUnit.DAYS);
        } else {
            problem = JSONUtil.toBean(problemJsonStr, Problem.class);
        }
        return Result.ok(problem);
    }

    @Override
    public Result<?> judge(ProblemCodeDto problemCodeDto) {
        // 把信息传到消息队列中
        codePublisher.publishCodeToQueue(problemCodeDto.getCode());
        return Result.ok();
    }

}
