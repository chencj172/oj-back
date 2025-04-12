package com.chencj.judge.model;


import lombok.Data;

/**
 * @ClassName: LanguageConfig
 * @Description: 语言对应的编译运行命令都封装在这个类里面
 * @Author: chencj
 * @Datetime: 2025/4/12 10:07
 * @Version: 1.0
 */
@Data
public class LanguageConfig {
    /**
     * 语言名称
     */
    private String language;
    /**
     * 源代码文件名称
     */
    private String srcName;
    /**
     *源代码可执行文件名称
     */
    private String exeName;
    /**
     * 最大CPU运行时间 s
     */
    private Long maxCpuTime;
    /**
     * 最大真实运行时间 s
     */
    private Long maxRealTime;
    /**
     * 程序最大内存占用
     */
    private Long maxMemory;
    /**
     * 编译源文件命令
     */
    private String compileCommand;
    /**
     * 运行可执行文件命令
     */
    private String runCommand;
    /**
     * 编译环境
     */
    private String compileEnv;
    /**
     * 运行环境
     */
    private String runEnv;
}
