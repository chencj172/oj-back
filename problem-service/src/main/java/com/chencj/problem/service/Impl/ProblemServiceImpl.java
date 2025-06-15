package com.chencj.problem.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.api.client.UserClient;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.constant.StringConstant;
import com.chencj.common.model.ProblemCodeDto;
import com.chencj.common.utils.Result;
import com.chencj.problem.mapper.ProblemMapper;
import com.chencj.problem.mapper.TagMapper;
import com.chencj.problem.model.po.Problem;
import com.chencj.problem.model.po.Tag;
import com.chencj.problem.model.vo.ProblemVo;
import com.chencj.problem.publisher.CodePublisher;
import com.chencj.problem.service.ProblemService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.chencj.common.constant.RedisConstant.TAG_INFO;

/**
 * @ClassName: ProblemService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/9 18:55
 * @Version: 1.0
 */
@Service
@Slf4j
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {

    @Resource
    private ProblemMapper problemMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CodePublisher codePublisher;

    @Resource
    private UserClient userClient;

    @Resource
    private TagMapper tagMapper;


    @Override
    public Result<?> search(String token, Integer level, String word, Integer pageNum, Integer pageSize) {
        Page<ProblemVo> page = new Page<>(pageNum, pageSize);
        Result<?> ret = userClient.checkLogin(token);
        Integer uid = null;
        if (ret.getCode() == 200) uid = (int) ret.getData();
        problemMapper.search(page, level, word, uid);
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
        // 生成一条记录评测状态的数据放到Redis里，待判题结束将判题结果返回给用户并且存到数据库中
        String Key = RedisConstant.PROBLEM_JUDGE + problemCodeDto.getUid() + ":" + problemCodeDto.getPid();
        // 如果之前有遗留的清除一下
        stringRedisTemplate.delete(Key);
        stringRedisTemplate.opsForHash().put(Key, "status", StringConstant.TESTCASE_STATUS_PADDING);
        // 把信息传到对应的消息队列中
        switch (problemCodeDto.getOrigin()) {
            case 2:
                codePublisher.publishDailyProblemCodeToQueue(JSONUtil.toJsonStr(problemCodeDto));
                break;
            case 3:
                codePublisher.publishPKCodeToQueue(JSONUtil.toJsonStr(problemCodeDto));
                break;
            case 1:
            default:
                codePublisher.publishJudgeCodeToQueue(JSONUtil.toJsonStr(problemCodeDto));
        }

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

    @Override
    public Result<?> getAllTag() {
        List<String> tags;
        if (stringRedisTemplate.hasKey(TAG_INFO)) {
            Map<Object, Object> tagMap = stringRedisTemplate.opsForHash().entries(TAG_INFO);
            tags = tagMap.values().stream().map((tag) -> (String) tag).toList();
        } else {
            tags = new ArrayList<>();
            List<Tag> allTag = tagMapper.getAllTag();
            Map<String, String> tagMap = new HashMap<>();
            for(Tag tag : allTag) {
                tags.add(tag.getTagName());
                tagMap.put(tag.getId().toString(), tag.getTagName());
            }
            stringRedisTemplate.opsForHash().putAll(TAG_INFO, tagMap);
        }
        return Result.ok(tags);
    }

}
