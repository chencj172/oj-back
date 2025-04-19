package com.chencj.problem.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.constant.StringConstant;
import com.chencj.common.model.ProblemCodeDto;
import com.chencj.common.utils.Result;
import com.chencj.problem.mapper.ProblemMapper;
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
        // 生成一条记录评测转态的数据放到Redis里，待判题结束将判题结果返回给用户并且存到数据库中，并且查看是否要更新Redis
        String Key = RedisConstant.PROBLEM_JUDGE + problemCodeDto.getUid() + ":" + problemCodeDto.getPid();
        // 如果之前有遗留的清除一下
        stringRedisTemplate.delete(Key);
        stringRedisTemplate.opsForHash().put(Key, "status", StringConstant.TESTCASE_STATUS_PADDING);
        // 把信息传到消息队列中
        codePublisher.publishJudgeCodeToQueue(JSONUtil.toJsonStr(problemCodeDto));
        return Result.ok();
    }

    @Override
    public Result<?> testCase(ProblemCodeDto problemCodeDto) {
        // 在Redis中生成该题目对应的测试结果
        String Key = RedisConstant.PROBLEM_TESTCASE + problemCodeDto.getUid() + ":" + problemCodeDto.getPid();
        // 如果之前有遗留的清除一下
        stringRedisTemplate.delete(Key);
        stringRedisTemplate.opsForHash().put(Key, "status", StringConstant.TESTCASE_STATUS_PADDING);

        // 将评测用例相关信息放到消息队列中
        codePublisher.publishTestCodeToQueue(JSONUtil.toJsonStr(problemCodeDto));
        return Result.ok();
    }

}
