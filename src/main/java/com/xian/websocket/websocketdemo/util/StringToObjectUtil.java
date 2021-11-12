package com.xian.websocket.websocketdemo.util;

import com.alibaba.fastjson.JSON;
import com.xian.websocket.websocketdemo.model.WebMsg;

/**
 * @author xian
 * @description
 * @createTime 2021/10/24 11:43
 */
public class StringToObjectUtil {

    public static WebMsg parseWebMsg(String msg) {
        return JSON.parseObject(msg,WebMsg.class);
    }
}
