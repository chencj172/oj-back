package com.chencj.judge.utils;


import cn.hutool.json.JSONArray;
import com.chencj.judge.model.LanguageConfig;

/**
 * @ClassName: Compile
 * @Description: 调用SandBoxRun沙箱进行代码编译
 * @Author: chencj
 * @Datetime: 2025/4/14 19:40
 * @Version: 1.0
 */
public class Compile {
    public static JSONArray compileCode(LanguageConfig languageConfig, String code) {
        return SandboxRun.compile(
                languageConfig.getSrcName(),
                languageConfig.getExeName(),
                LanguageConfigLoader.splitBySpace(languageConfig.getCompileCommand()),
                LanguageConfigLoader.splitBySpace(languageConfig.getCompileEnv()),
                languageConfig.getMaxCpuTime() * 1000,
                languageConfig.getMaxMemory(),
                256 * 1024 * 1024L,
                10L,
                languageConfig.getMaxRealTime() * 1000,
                code);
    }
}
