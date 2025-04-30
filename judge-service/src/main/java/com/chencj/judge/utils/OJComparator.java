package com.chencj.judge.utils;


/**
 * @ClassName: OJComparator
 * @Description: 比较程序输出和答案是否一致，自动去掉多余空格以及换行
 * @Author: chencj
 * @Datetime: 2025/4/16 16:29
 * @Version: 1.0
 */
public class OJComparator {
    /**
     * 比较两个字符串的"有效内容"，忽略空白字符差异
     *
     * @param actual   程序实际输出
     * @param expected 期望输出
     * @return 当有效内容相同时返回true
     */
    public static boolean compareOutput(String actual, String expected) {
        // 处理null值情况
        if (actual == null || expected == null) {
            return actual == expected;
        }

        // 标准化处理两个字符串
        String processedActual = normalize(actual);
        String processedExpected = normalize(expected);

        return processedActual.equals(processedExpected);
    }

    /**
     * 标准化字符串处理：
     * 1. 所有连续空白字符替换为单个空格
     * 2. 去除首尾空白
     */
    private static String normalize(String input) {
        // 替换所有连续空白字符为单个空格
        String normalized = input.replaceAll("\\s+", " ");
        // 去除首尾空格
        return normalized.trim();
    }
}