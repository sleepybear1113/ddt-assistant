package cn.sleepybear.ddtassistant.socket;

import cn.sleepybear.ddtassistant.utils.Util;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/11 16:27
 */

@Slf4j
@ServerEndpoint(value = "/serverLogSocket")
@Component
public class WebSocketServer {

    private static volatile int onlineCount = 0;
    private final static CopyOnWriteArraySet<WebSocketServer> WEB_SOCKET_SET = new CopyOnWriteArraySet<>();
    private Session session;

    /**
     * 连接建立成功调用的方法
     *
     * @param session 连接会话
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        // 加入set中
        WEB_SOCKET_SET.add(this);
        // 连接数加1
        addOnlineCount();
        log.info("连接到服务器，当前连接数为" + getOnlineCount());
        sendMessage("连接成功");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        // 从set中删除
        WEB_SOCKET_SET.remove(this);
        // 连接数减1
        subOnlineCount();
        log.info("有一连接关闭！当前连接数为：" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 消息
     * @param session 连接会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自客户端的消息：" + message);

        // 群发消息
        for (WebSocketServer item : WEB_SOCKET_SET) {
            item.sendMessage(message);
        }
    }

    /**
     * 发生错误时调用的方法
     *
     * @param session 连接会话
     * @param error   错误信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发送错误 {}", error.getMessage());
    }

    /**
     * 实现服务器主动推送消息
     *
     * @param message 消息
     * @throws IOException
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(String.format("[%s] %s", Util.getTimeString(Util.TIME_ALL_FORMAT_EASY), message));
        } catch (Exception e) {
            log.warn("sessionId = [{}] {}", this.session.getId(), e.getMessage());
        }
    }

    public static void sendMessageAll(String message) {
        for (WebSocketServer item : WEB_SOCKET_SET) {
            item.sendMessage(message);
        }
    }

    /**
     * 群发自定义消息
     *
     * @param message 自定义消息
     * @throws IOException
     */
    public static void sendInfo(String message) throws IOException {
        log.info("推送消息内容：" + message);
        for (WebSocketServer item : WEB_SOCKET_SET) {
            item.sendMessage(message);
        }
    }

    /**
     * 获取连接数
     *
     * @return 连接数
     */
    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 连接数加1
     */
    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    /**
     * 连接数减1
     */
    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketServer that = (WebSocketServer) o;
        return Objects.equals(session.getId(), that.session.getId());
    }

    @Override
    public int hashCode() {
        return session != null ? session.hashCode() : 0;
    }
}
