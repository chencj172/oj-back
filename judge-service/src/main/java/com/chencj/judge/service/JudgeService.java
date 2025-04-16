package com.chencj.judge.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.constant.StringConstant;
import com.chencj.common.model.ProblemCodeDto;
import com.chencj.judge.model.LanguageConfig;
import com.chencj.judge.utils.Compile;
import com.chencj.judge.utils.JudgeRun;
import com.chencj.judge.utils.LanguageConfigLoader;
import com.chencj.judge.utils.SandboxRun;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * @ClassName: JudgeService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/11 15:41
 * @Version: 1.0
 */
@Service
public class JudgeService {

    @Resource
    private LanguageConfigLoader languageConfigLoader;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String judgeProblem(String code) {
        return "";
    }

    public Map<Object, Object> getTestCaseStatus(String Key) {
        return stringRedisTemplate.opsForHash().entries(Key);
    }

    /**
     * 测试用例
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
        Map<String, String> runResult = JudgeRun.runCode(languageConfig, fileId, problemCodeDto);
        stringRedisTemplate.opsForHash().putAll(Key, runResult);

        // 删除临时文件，防止内存泄漏
        SandboxRun.desFile(fileId);
    }
}
