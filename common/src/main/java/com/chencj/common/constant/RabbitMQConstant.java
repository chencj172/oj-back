package com.chencj.common.constant;


/**
 * @ClassName: RabbitMQConstant
 * @Description: 消息队列中的交换机和队列名称
 * @Author: chencj
 * @Datetime: 2025/4/11 14:33
 * @Version: 1.0
 */
public class RabbitMQConstant {
    // 判题交换机
    public final static String CODE_EXCHANGE = "judge.direct";
    public final static String CODE_ROUTING_KEY = "judge";

    // 运行测试样例交换机
    public final static String TESTCASE_CODE_EXCHANGE = "testcase.direct";
    public final static String TESTCASE_CODE_ROUTING_KEY = "testcase";
}
