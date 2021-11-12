package com.xian.websocket.websocketdemo.handle;

import com.alibaba.fastjson.JSON;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.xdevapi.Client;
import com.xian.websocket.websocketdemo.config.WebSessionManager;
import com.xian.websocket.websocketdemo.constant.ClientStatus;
import com.xian.websocket.websocketdemo.constant.Commons;
import com.xian.websocket.websocketdemo.constant.MsgType;
import com.xian.websocket.websocketdemo.constant.RobotName;
import com.xian.websocket.websocketdemo.model.PkRoom;
import com.xian.websocket.websocketdemo.model.QuestionModel;
import com.xian.websocket.websocketdemo.model.WebMsg;
import com.xian.websocket.websocketdemo.service.QuestionService;
import com.xian.websocket.websocketdemo.util.StringToObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * 消息处理类
 * @author xian
 * @description
 * @createTime 2021/10/24 11:19
 */
@Slf4j
@Component
public class WebMsgHandler {

    @Resource
    private QuestionService questionService;

    public void handle(WebSocketSession session, WebSocketMessage<?> msg) throws IOException {
        TextMessage message = (TextMessage) msg;
        // 客户端传的json数据
        WebMsg webMsg = StringToObjectUtil.parseWebMsg(message.getPayload());
        if (webMsg == null || StringUtils.isNullOrEmpty(webMsg.getUsername())) {
            // 消息为空或者用户名为空
            log.info("消息错误{}",webMsg);
        }
        TextMessage result = null;
        String type = webMsg.getType();
        String username = webMsg.getUsername();
        if (MsgType.REGISTER.equals(type)) {
            // 处理注册消息
            WebSocketSession webSocketSession = WebSessionManager.get(username);
            if (webSocketSession != null && !webSocketSession.isOpen()) {
                    result = new TextMessage(new WebMsg().setData("昵称重复了").setType(MsgType.ERROR).toJsonString());
            } else {
                WebSessionManager.add(webMsg.getUsername(),session);
                result = new TextMessage(new WebMsg().setData("register success").setType(MsgType.REG_SUCCESS).toJsonString());
            }
            session.sendMessage(result);
        } else if (MsgType.PK.equals(type)){
            pk(webMsg,username,session);
        } else if (MsgType.LEAVE.equals(type)) {
            // 离开房间,把对应房间的位置空出来，但不断开连接
            log.info("离开房间{}",username);
            WebSessionManager.leaveRoom(username);
            session.sendMessage(new TextMessage(new WebMsg().setData("离开房间")
                    .setUsername(username)
                    .setType(MsgType.SUCCESS)
                    .toJsonString()));
        } else if (MsgType.SCORE.equals(type)) {
            log.info("开始处理分数请求，请求客户端[{}]",username);
            // 发送分数
            PkRoom room = WebSessionManager.getRoom(webMsg.getRoomNo());
            if (null != room) {
                WebMsg webMsg1 = new WebMsg()
                        .setData(webMsg.getData())
                        .setRoomNo(webMsg.getRoomNo())
                        .setType(MsgType.SCORE).setUsername(username);
                WebSocketSession webSocketSession = null;
                if (username.equals(room.getSessionOne())) {
                    webSocketSession = WebSessionManager.get(room.getSessionTwo());
                } else if (username.equals(room.getSessionTwo())){
                    webSocketSession = WebSessionManager.get(room.getSessionOne());
                }
                // 有可能你发生分数的时候，对手已经离开了房间，所以眼判断是否为空
                if (webSocketSession!=null) {
                    webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(webMsg1)));
                }
            }
        } else if (MsgType.QUESTIONS.equals(type)){
            // 客户端匹配等待超时，直接发送请求题目列表，这个时候要保证此客户端不在房间里，保险起见，再次尝试清理出房间
            log.info("用户{}匹配超时，请求题目列表",username);
            sendQuestions(session,username);
        }
    }

    private void sendQuestions(WebSocketSession session,String username) throws IOException {
        // 先清理出房间
        WebSessionManager.leaveRoom(username);
        // 获取题目列表
        List<QuestionModel> questionList = questionService.listRandomQuestion(5);
        // 客户端看到的机器人的昵称
        StringBuilder stringBuilder = new StringBuilder();
        if (username.length() % Commons.OU_NUM == 0) {
            stringBuilder.append(RobotName.A);
        } else {
            stringBuilder.append(RobotName.B);
        }
        stringBuilder.append(new Random().nextInt(101));
        // 返回信息
        TextMessage message = new TextMessage(new WebMsg()
                .setData(JSON.toJSONString(questionList))
                .setType(MsgType.QUESTIONS).setUsername(stringBuilder.toString()).toJsonString());
        session.sendMessage(message);
        log.info("下发题目成功,用户{}",username);
    }

    /**
     * 处理PK请求
     * @param webMsg 消息实体
     * @param username 用户名
     * @param session 连接
     * @throws IOException
     */
    private void pk(WebMsg webMsg,String username,WebSocketSession session) throws IOException {
        // 处理请求PK消息
        // 检验
        TextMessage result = null;
        log.info("开始处理PK请求，请求客户端[{}]",username);
        WebSocketSession userSession = WebSessionManager.get(webMsg.getUsername());
        if (userSession == null) {
            // 绑定
            WebSessionManager.add(webMsg.getUsername(),session);
        }
        // 搜寻房间
        String s = WebSessionManager.checkRoom(username);
        PkRoom pkRoom = null;
        if (StringUtils.isNullOrEmpty(s)) {
            pkRoom = WebSessionManager.setInRoom(username);
        } else {
            // 已经在房间的，不发送,因为前端页面只请求一次，
            pkRoom = WebSessionManager.getRoom(s);
            if (pkRoom != null && !pkRoom.checkEmpty()) {
                result = new TextMessage(new WebMsg().setData(pkRoom.getRoomNo())
                        .setUsername(username.equals(pkRoom.getSessionOne())
                                ? pkRoom.getSessionTwo():pkRoom.getSessionOne())
                        .setType(MsgType.PK_REST).toJsonString());
                session.sendMessage(result);
                return;
            }
        }
        TextMessage questions = null;
        // 匹配成功
        if (pkRoom != null && !pkRoom.checkEmpty() && ClientStatus.READY.equals(pkRoom.getStatus())) {
            // 获取两个用户的session
            WebSocketSession session1 = WebSessionManager.get(pkRoom.getSessionOne());
            WebSocketSession session2 = WebSessionManager.get(pkRoom.getSessionTwo());
            String roomNo = pkRoom.getRoomNo();
            if (session1 != null && session2 != null
                    && session1.isOpen() && session2.isOpen() ) {
                log.info("用户[{}]和[{}]匹配成功，房间号[{}]",pkRoom.getSessionOne(),pkRoom.getSessionTwo(),roomNo);
                // 发送对手昵称和房间号
                List<QuestionModel> questionList = questionService.listRandomQuestion(5);
                questions = new TextMessage(new WebMsg().setData(JSON.toJSONString(questionList))
                        .setType(MsgType.QUESTIONS)
                        .setRoomNo(roomNo).toJsonString());
                // 发送对方的昵称
                session1.sendMessage(new TextMessage(new WebMsg().setData(roomNo)
                        .setUsername(username.equals(pkRoom.getSessionOne())
                                ? pkRoom.getSessionOne():pkRoom.getSessionTwo())
                        .setType(MsgType.PK_REST)
                        .setRoomNo(roomNo).toJsonString()));
                session2.sendMessage(new TextMessage(new WebMsg().setData(roomNo)
                        .setUsername(username.equals(pkRoom.getSessionOne())
                                ? pkRoom.getSessionTwo():pkRoom.getSessionOne())
                        .setType(MsgType.PK_REST)
                        .setRoomNo(roomNo).toJsonString()));
                // 发送题目
                session1.sendMessage(questions);
                session2.sendMessage(questions);
                log.info("发送题目成功，客户端[{}]和[{}],用户[{}]和[{}]",session1.getId(),session2.getId(),pkRoom.getSessionOne()
                        ,pkRoom.getSessionTwo());
            }

        }
    }

}
