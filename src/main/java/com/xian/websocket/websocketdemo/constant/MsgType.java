package com.xian.websocket.websocketdemo.constant;

/**
 * @author xian
 * @description
 * @createTime 2021/10/24 11:36
 */
public interface MsgType {

    /**
     * 客户端注册信息
     */
    String REGISTER = "1";

    /**
     * 注册成功
     */
    String REG_SUCCESS = "11";

    /**
     * 发送分数
     */
    String SCORE = "2";

    /**
     * 发送题目
     */
    String QUESTIONS = "3";

    /**
     * 请求匹配
     */
    String PK = "4";
    /**
     * 匹配成功
     */
    String PK_REST = "41";

    /**
     * PK就绪
     */
    String REST = "5";

    /**
     * PK离开
     */
    String LEAVE = "6";

    String SUCCESS = "100";

    String ERROR = "200";

}
