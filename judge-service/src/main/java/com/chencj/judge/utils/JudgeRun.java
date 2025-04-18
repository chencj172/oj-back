package com.chencj.judge.utils;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.chencj.common.constant.StringConstant;
import com.chencj.common.model.ProblemCodeDto;
import com.chencj.judge.model.LanguageConfig;

import java.util.*;

/**
 * @ClassName: JuderRun
 * @Description: 调用SandBoxRun沙箱运行代码，用于测试样例
 * @Author: chencj
 * @Datetime: 2025/4/14 19:40
 * @Version: 1.0
 */
public class JudgeRun {
    /**
     * 评测用例，如果状态是Accepted直接返回结果就好，否则返回错误信息
     *
     * @param languageConfig
     * @param fileId
     * @param problemCodeDto
     * @return
     */
    public static Map<String, String> testCase(
            LanguageConfig languageConfig,
            String fileId,
            ProblemCodeDto problemCodeDto) {
        Map<String, String> ret = new HashMap<>();
        if (languageConfig.getLanguage() == null
                || (!languageConfig.getLanguage().equals("c") && !languageConfig.getLanguage().equals("C++"))) {
            problemCodeDto.setTimeLimit(problemCodeDto.getTimeLimit() * 2);
            problemCodeDto.setMemoryLimit(problemCodeDto.getMemoryLimit() * 2);
            problemCodeDto.setStackLimit(problemCodeDto.getStackLimit() * 2);
        }

        // 调用沙箱运行代码获取运行结果
        JSONArray run = SandboxRun.run(
                languageConfig.getExeName(),
                fileId,
                LanguageConfigLoader.splitBySpace(languageConfig.getRunCommand()),
                LanguageConfigLoader.splitBySpace(languageConfig.getRunEnv()),
                problemCodeDto.getInput(),
                (long) problemCodeDto.getTimeLimit() * 1000,
                (long) (problemCodeDto.getMemoryLimit() * 1024 * 1024),
                (long) (problemCodeDto.getStackLimit() * 1000),
                10L,
                languageConfig.getMaxRealTime() * 1000);
        JSONObject runObj = (JSONObject) run.get(0);
        String runStatus = runObj.getStr("status");
        JSONObject filesObj = (JSONObject) runObj.get("files");
        ret.put("status", runStatus);
        if (!StringConstant.ACCEPTED.equals(runStatus)) {
            // 运行出错将错误信息填写进去然后直接返回
            ret.put("runError", filesObj.getStr("stderr"));
            return ret;
        }

        // 走到这说明测试完成了
        ret.put("status", StringConstant.FINISHED);
        ret.put("testCaseResult", filesObj.getStr("stdout"));
        return ret;
    }

    /**
     * 判题
     * @param languageConfig
     * @param fileId
     * @param problemCodeDto
     * @return
     */
    public static Map<String, String> runCode(
            LanguageConfig languageConfig,
            String fileId,
            ProblemCodeDto problemCodeDto) {
        Map<String, String> ret = new HashMap<>();
        // 运行每一个输入，然后比较对应的输出
        List<String> inputList = splitByDoubleNewline(problemCodeDto.getInput());
        List<String> outputList = splitByDoubleNewline(problemCodeDto.getOutput());

        if (languageConfig.getLanguage() == null
                || (!languageConfig.getLanguage().equals("c") && !languageConfig.getLanguage().equals("C++"))) {
            problemCodeDto.setTimeLimit(problemCodeDto.getTimeLimit() * 2);
            problemCodeDto.setMemoryLimit(problemCodeDto.getMemoryLimit() * 2);
            problemCodeDto.setStackLimit(problemCodeDto.getStackLimit() * 2);
        }

        for (int i = 0; i < inputList.size(); i++) {
            String inputStr = inputList.get(i);
            String outputStr = outputList.get(i);

            JSONArray run = SandboxRun.run(
                    languageConfig.getExeName(),
                    fileId,
                    LanguageConfigLoader.splitBySpace(languageConfig.getRunCommand()),
                    LanguageConfigLoader.splitBySpace(languageConfig.getRunEnv()),
                    inputStr,
                    (long) problemCodeDto.getTimeLimit() * 1000,
                    (long) (problemCodeDto.getMemoryLimit() * 1024 * 1024),
                    (long) (problemCodeDto.getStackLimit() * 1000),
                    10L,
                    languageConfig.getMaxRealTime() * 1000);
            JSONObject runObj = (JSONObject) run.get(0);
            String runStatus = runObj.getStr("status");
            ret.put("status", runStatus);
            if (!StringConstant.ACCEPTED.equals(runStatus)) {
                // 判题错误的样例、错误的结果、正确的结果放到Map里
                ret.put("result", runStatus);
                ret.put("errorInput", inputStr);
                ret.put("correctOutput", outputStr);
                return ret;
            } else {
                // 获取输出结果
                JSONObject filesObj = (JSONObject) runObj.get("files");
                String runResult = filesObj.getStr("stdout");
                if (StrUtil.isBlank(runResult) || !OJComparator.compareOutput(runResult, outputStr)) {
                    // 判题错误的样例、错误的结果、正确的结果放到Map里
                    ret.put("result", "WorryAnswer");
                    ret.put("errorInput", inputStr);
                    ret.put("errorOutput", runResult);
                    ret.put("correctOutput", outputStr);
                    return ret;
                }
            }
        }

        ret.put("result", StringConstant.ACCEPTED);
        return ret;
    }

    /**
     * 按 "\n\n" 分割字符串并返回列表
     *
     * @param Str 输入字符串（例如 "2\n1 2\n\n3\n4 54\n\n5\n2 3\n\n"）
     * @return 分割后的字符串列表
     */
    public static List<String> splitByDoubleNewline(String Str) {
        // 处理空输入
        if (Str == null || Str.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        // 按 "\n\n" 分割，并移除空字符串
        String[] parts = Str.split("\n\n", -1); // -1 保留末尾空串
        for (String part : parts) {
            // Replace any remaining "\n\n" with "\n" (though split should have handled this)
            String processedPart = part.replace("\n\n", "");
            result.add(processedPart);
        }

        // 移除所有纯空白字符串（可选，根据需求调整）
        result.removeIf(String::isEmpty);

        return result;
    }
}
