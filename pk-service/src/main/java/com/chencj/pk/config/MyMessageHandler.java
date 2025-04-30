package com.chencj.pk.config;

import com.chencj.common.constant.RedisConstant;
import com.chencj.common.constant.StringConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class MyMessageHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<Object, WebSocketSession> userMap;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    static {
        userMap = new ConcurrentHashMap<>();
    }

    public Map.Entry<Object, WebSocketSession> randomUser() {
        if (userMap == null || userMap.isEmpty()) {
            return null;
        }

        int size = userMap.size();
        int randomIndex = ThreadLocalRandom.current().nextInt(size);
        int index = 0;
        Map.Entry<Object, WebSocketSession> res = null;

        for (Map.Entry<Object, WebSocketSession> m : userMap.entrySet()) {
            if(index++ == randomIndex) {
                res = m;
                break;
            }
        }

        return res;
    }

    /**
     * socket 建立成功事件 @OnOpen
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object userId = session.getAttributes().get("userId");
        // 从已连接的用户中挑选一个进行挑战，并且在redis生成两条记录
        // key中包含挑战双方的userId，value是一个哈希表，哈希表存储的是对方的userId，题目通过状态，题目第一次通过的时间戳，挑战的结果
        if(userMap.size() > 1) {
            Map.Entry<Object, WebSocketSession> challengeUser = randomUser();
            recordToRedis(userId, challengeUser.getKey());
            // 通知双方选取的题目Id
            session.sendMessage(new TextMessage("204"));
            challengeUser.getValue().sendMessage(new TextMessage("204"));
        } else {
            // 没有的话就先放到用户在线列表中
            userMap.put(userId, session);
            session.sendMessage(new TextMessage("padding"));
        }
        // 发送匹配成功的消息（挑战的双方）
    }

    private void recordToRedis(Object userId, Object challengeId) {
        String Key1 = RedisConstant.CHALLENGE_RECORD + userId;
        String Key2 = RedisConstant.CHALLENGE_RECORD + challengeId;
        Map<String, String> map1 = new HashMap<>();
        map1.put(RedisConstant.OTHER_SIDE, (String) challengeId);
        map1.put("judgeresult", StringConstant.TESTCASE_STATUS_PADDING);
        map1.put("timestamp", "");
        map1.put("challengeresult", StringConstant.TESTCASE_STATUS_PADDING);
        Map<String, String> map2 = new HashMap<>();
        map2.put(RedisConstant.OTHER_SIDE, (String) userId);
        map2.put("judgeresult", StringConstant.TESTCASE_STATUS_PADDING);
        map2.put("timestamp", "");
        map2.put("challengeresult", StringConstant.TESTCASE_STATUS_PADDING);

        stringRedisTemplate.opsForHash().putAll(Key1, map1);
        stringRedisTemplate.opsForHash().putAll(Key2, map2);
    }

    /**
     * 接收消息事件 @OnMessage
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        session.sendMessage(new TextMessage("server 发送消息 " + payload + " " + LocalDateTime.now()));
    }

    /**
     * 点对点发送
     *
     * @param userId
     * @param msg
     * @throws IOException
     */
    private void send(String userId, String msg) throws IOException {
        WebSocketSession webSocketSession = userMap.get(userId);
        if(webSocketSession != null) {
            webSocketSession.sendMessage(new TextMessage(msg));
        }
    }

    /**
     * 广播
     *
     * @param msg
     * @throws IOException
     */
    private void broadcast(String msg) throws IOException {
        for(WebSocketSession session : userMap.values()) {
            session.sendMessage(new TextMessage(msg));
        }
    }

    /**
     * socket 断开连接时 @OnClose
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("断开连接");
        Object userId = session.getAttributes().get("userId");
        if (userId != null) {
            // 用户退出，移除缓存
            userMap.remove(userId);
        }
    }

}