package com.chencj.judge.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.chencj.api.client.ProblemClient;
import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.constant.StringConstant;
import com.chencj.common.model.ProblemCodeDto;
import com.chencj.common.utils.SimpleRedisLock;
import com.chencj.judge.model.LanguageConfig;
import com.chencj.judge.utils.Compile;
import com.chencj.judge.utils.JudgeRun;
import com.chencj.judge.utils.LanguageConfigLoader;
import com.chencj.judge.utils.SandboxRun;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName: JudgeService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/11 15:41
 * @Version: 1.0
 */
@Service
@Slf4j
public class JudgeService {

    @Resource
    private LanguageConfigLoader languageConfigLoader;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ProblemClient problemClient;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));


    /**
     * 判题
     * @param problemCodeDto
     */
    public void judgeProblem(ProblemCodeDto problemCodeDto) {
        // 用于更新判题的状态
        String Key = RedisConstant.PROBLEM_JUDGE + problemCodeDto.getUid() + ":" + problemCodeDto.getPid();
        stringRedisTemplate.opsForHash().put(Key, "status", StringConstant.TESTCASE_STATUS_PADDING);
        // 分布式锁，防止用户没判完题重复提交
        SimpleRedisLock simpleRedisLock = new SimpleRedisLock(stringRedisTemplate, StringConstant.TESTCASE_STATUS_PADDING);
        boolean lock = simpleRedisLock.tryLock(1);
        if(!lock) {
            // 没拿到锁不允许判题，直接返回
            return ;
        }

        try {
            // 先编译
            LanguageConfig languageConfig = languageConfigLoader.getLanguageConfigByName(problemCodeDto.getLanguage());
            JSONArray compile = Compile.compileCode(languageConfig, problemCodeDto.getCode());
            JSONObject compileResult = (JSONObject) compile.get(0);
            String status = compileResult.getStr("status");
            JudgeRecord judgeRecord = new JudgeRecord();
            judgeRecord.setUid(problemCodeDto.getUid());
            judgeRecord.setPid(problemCodeDto.getPid());
            judgeRecord.setCode(problemCodeDto.getCode());
            judgeRecord.setCreateTime(LocalDateTime.now());
            judgeRecord.setLanguage(problemCodeDto.getLanguage());

            if (!StringConstant.ACCEPTED.equals(status)) {
                // 编译出错
                judgeRecord.setJudgeResult(status);
            } else {
                // 进行判题，更新judgeRecord
                JSONObject fileIds = (JSONObject) compileResult.get("fileIds");
                String fileId = fileIds.getStr(languageConfig.getExeName());
                JudgeRun.runCode(languageConfig, fileId, problemCodeDto, judgeRecord);

                // 删除文件
                SandboxRun.desFile(fileId);
            }

            // 放到线程池里面去执行更新操作
            threadPool.execute(new UpdateJudgeRecord(judgeRecord, stringRedisTemplate, problemClient));

        } catch (Exception e) {
            log.error("{}", e.getMessage());
        } finally {
            simpleRedisLock.unLock();
        }
    }

    public Map<Object, Object> getTestCaseStatus(String Key) {
        return stringRedisTemplate.opsForHash().entries(Key);
    }

    /**
     * 运行测试用例
     */
    public void testProblemCase(ProblemCodeDto problemCodeDto) {
        // 用于更新测试用例的结果
        String Key = RedisConstant.PROBLEM_TESTCASE + problemCodeDto.getUid() + ":" + problemCodeDto.getPid();
        // 先进行编译
        LanguageConfig languageConfig = languageConfigLoader.getLanguageConfigByName(problemCodeDto.getLanguage());
        JSONArray compile = Compile.compileCode(languageConfig, problemCodeDto.getCode());

        // 拿到编译的结果
        JSONObject compileResult = (JSONObject) compile.get(0);
        String status = compileResult.getStr("status");
        JSONObject filesJsonObj = (JSONObject) compileResult.get("files");
        if (!StringConstant.ACCEPTED.equals(status)) {
            // 非正常情况，更新状态直接返回
            stringRedisTemplate.opsForHash().put(Key, "status", status);
            stringRedisTemplate.opsForHash().put(Key,"compile-error", filesJsonObj.getStr("stderr"));
            return ;
        }

        // 编译成功进行判题
        JSONObject fileIds = (JSONObject) compileResult.get("fileIds");
        String fileId = fileIds.getStr(languageConfig.getExeName());
        Map<String, String> runResult = JudgeRun.testCase(languageConfig, fileId, problemCodeDto);
        stringRedisTemplate.opsForHash().putAll(Key, runResult);
        // 过期时间设置为1分钟
        stringRedisTemplate.expire(Key, 1, TimeUnit.MINUTES);

        // 删除临时文件，防止内存泄漏
        SandboxRun.desFile(fileId);
    }

    public Map<Object, Object> getJudgeStatus(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }
}
