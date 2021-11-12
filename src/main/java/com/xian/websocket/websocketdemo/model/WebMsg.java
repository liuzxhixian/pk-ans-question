package com.xian.websocket.websocketdemo.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xian
 * @description
 * @createTime 2021/10/24 11:06
 */
@Data
@Accessors(chain = true)
public class WebMsg implements Serializable {

    private String username;

    private String type;

    private Object data;

    /**
     * 房间编号
     */
    private String roomNo;

    /**
     * 转化成json字符串
     * @return
     */
    public String toJsonString() {
        return JSON.toJSONString(this);
    }

}
