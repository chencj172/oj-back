package com.chencj.judge.utils;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chencj.judge.model.LanguageConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @ClassName: LanguageConfigLoader
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/12 10:19
 * @Version: 1.0
 */
@Component
@Slf4j
public class LanguageConfigLoader {
    private static AtomicBoolean init = new AtomicBoolean(false);

    private static HashMap<String, LanguageConfig> languageConfigMap;

    @PostConstruct
    public void init() {
        if (init.compareAndSet(false, true)) {
            Iterable<Object> languageConfigIter = LoadYml("language.yml");
            // 封装LanguageConfig对象
            languageConfigMap = new HashMap<>();
            for (Object configObj : languageConfigIter) {
                JSONObject jsonObject = JSONUtil.parseObj(configObj);
                LanguageConfig languageConfig = buildLanguageConfig(jsonObject);
                languageConfigMap.put(languageConfig.getLanguage(), languageConfig);
            }
            log.info("languageConfig : {}", languageConfigMap);
        }
    }

    public LanguageConfig getLanguageConfigByName(String language) {
        return languageConfigMap.get(language);
    }

    /**
     * 将字符串按空格分隔到List中（处理多个连续空格）
     * @param input 输入字符串
     * @return 分隔后的List，如果输入为null则返回空List
     */
    public static List<String> splitBySpace(String input) {
        if (input == null || input.trim().isEmpty()) {
            return List.of(); // Java 9+ 或使用 new ArrayList<>()
        }

        // 使用正则表达式 \\s+ 匹配一个或多个空白字符
        return Arrays.stream(input.trim().split("\\s+"))
                .collect(Collectors.toList());
    }

    private LanguageConfig buildLanguageConfig(JSONObject jsonObject) {
        LanguageConfig languageConfig = new LanguageConfig();
        languageConfig.setLanguage(jsonObject.getStr("language"));
        languageConfig.setSrcName(jsonObject.getStr("src_path"));
        languageConfig.setExeName(jsonObject.getStr("exe_path"));

        // 封装编译环境
        JSONObject compileJsonObj = jsonObject.getJSONObject("compile");
        languageConfig.setCompileCommand(compileJsonObj.getStr("command"));
        languageConfig.setCompileEnv(compileJsonObj.getStr("env"));
        languageConfig.setMaxCpuTime(parseTimeStr(jsonObject.getStr("maxCpuTime")));
        languageConfig.setMaxRealTime(parseTimeStr(jsonObject.getStr("maxRealTime")));
        languageConfig.setMaxMemory(parseMemoryStr(jsonObject.getStr("maxMemory")));

        // 封装运行环境
        JSONObject runJsonObj = jsonObject.getJSONObject("run");
        languageConfig.setRunEnv(runJsonObj.getStr("env"));
        languageConfig.setRunCommand(runJsonObj.getStr("command"));

        return languageConfig;
    }

    private Long parseTimeStr(String timeStr) {
        if (StrUtil.isBlank(timeStr)) {
            return 3000L;
        }
        timeStr = timeStr.toLowerCase();
        if (timeStr.endsWith("s")) {
            return Long.parseLong(timeStr.replace("s", "")) * 1000;
        } else if (timeStr.endsWith("ms")) {
            return Long.parseLong(timeStr.replace("s", ""));
        } else {
            return Long.parseLong(timeStr);
        }
    }

    private Long parseMemoryStr(String memoryStr) {
        if (StrUtil.isBlank(memoryStr)) {
            return 256 * 1024 * 1024L;
        }
        memoryStr = memoryStr.toLowerCase();
        if (memoryStr.endsWith("mb")) {
            return Long.parseLong(memoryStr.replace("mb", "")) * 1024 * 1024;
        } else if (memoryStr.endsWith("kb")) {
            return Long.parseLong(memoryStr.replace("kb", "")) * 1024;
        } else if (memoryStr.endsWith("b")) {
            return Long.parseLong(memoryStr.replace("b", ""));
        } else {
            return Long.parseLong(memoryStr) * 1024 * 1024;
        }
    }

    private Iterable<Object> LoadYml(String fileName) {
        Yaml yaml = new Yaml();
        String ymlContent = ResourceUtil.readUtf8Str(fileName);
        return yaml.loadAll(ymlContent);
    }
}
