// package com.chencj.pk.Websocket;
//
//
// import jakarta.websocket.OnClose;
// import jakarta.websocket.OnMessage;
// import jakarta.websocket.OnOpen;
// import jakarta.websocket.Session;
// import jakarta.websocket.server.ServerEndpoint;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Component;
//
// import java.io.IOException;
// import java.util.concurrent.ConcurrentHashMap;
//
// /**
//  * @ClassName: PkSocket
//  * @Description:
//  * @Author: chencj
//  * @Datetime: 2025/4/27 15:06
//  * @Version: 1.0
//  */
// @Slf4j
// @Component
// @ServerEndpoint("/pk/challenge")
// public class PkSocket {
//
//     // 统计发起pk的用户
//     private static ConcurrentHashMap<PkSocket, PkSocket> PK_USER_MAP;
//
//     static {
//         PK_USER_MAP = new ConcurrentHashMap<>();
//     }
//
//     // 会话的session
//     private Session session;
//
//     /**
//      * 客户端连接调用的函数
//      * @param session
//      */
//     @OnOpen
//     public void open(Session session) {
//         this.session = session;
//     }
//
//     /**
//      * 接收消息
//      * @param message
//      */
//     @OnMessage
//     public void recvMessage(String message) {
//         log.info("recv message: {}", message);
//     }
//
//     /**
//      * 发送
//      * @param message
//      */
//     public void send(String message) {
//         try {
//             this.session.getBasicRemote().sendText(message);
//         } catch (IOException e) {
//             log.error("send message to client error : {}", e.getMessage());
//         }
//     }
//
//     @OnClose
//     public void clos() {
//         this.session = null;
//         log.info("一个用户退出");
//     }
// }
