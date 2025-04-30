package com.chencj.common.constant;


/**
 * @ClassName: RedisConstant
 * @Description: Redis中的前缀Key
 * @Author: chencj
 * @Datetime: 2025/4/10 20:27
 * @Version: 1.0
 */
public class RedisConstant {
    // 用户相关
    public final static String USER_INFO = "user:info:";
    public final static String USER_AC_PROBLEM_LIST = "user:aclist:";
    public final static String USER_SIGNIN = "user:sign:";

    // 题目相关
    public final static String PROBLEM_INFO_DETAIL = "problem:info:";
    public final static String PROBLEM_TESTCASE = "problem:testcase:";
    public final static String PROBLEM_JUDGE = "problem:judge:";
    public final static String PROBLEM_JUDGE_DETAIL = "problem:judge:detail:";
    public final static String PROBLEM_JUDGE_RECORD_LIST = "problem:judgerecord:";
    public final static String DAILY_PROBLEM_OF_MONTH = "problem:dailylist:";

    // 挑战相关
    public final static String CHALLENGE_RECORD = "challenge:record:";
    public final static String OTHER_SIDE = "otherside";
}
