package com.xian.websocket.websocketdemo.handle;

import com.xian.websocket.websocketdemo.config.WebSessionManager;
import com.xian.websocket.websocketdemo.model.WebMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.annotation.Resource;

/**
 * @author xian
 * @description
 * @createTime 2021/10/21 17:57
 */
@Slf4j
@Component
public class MyWsHandler extends AbstractWebSocketHandler {

    @Resource
    private WebMsgHandler msgHandler;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[{}]已连接",session.getId());
        TextMessage message = new TextMessage(new WebMsg().setData("hello client").toJsonString());
        session.sendMessage(message);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("收到消息{}",message.getPayload());
        msgHandler.handle(session,message);
        super.handleMessage(session, message);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
        System.out.println("发生错误");
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("[{}]已断开",session.getId());
        WebSessionManager.removeByData(session);
        super.afterConnectionClosed(session, status);
    }
}
