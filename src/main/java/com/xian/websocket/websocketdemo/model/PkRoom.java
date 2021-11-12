package com.xian.websocket.websocketdemo.model;

import com.xian.websocket.websocketdemo.constant.ClientStatus;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author xian
 * @description
 * @createTime 2021/10/24 15:53
 */
@Data
@Accessors(chain = true)
public class PkRoom {

    /**
     *
     */
    private String roomNo;
    /**
     * 房间状态
     */
    private String status;

    private String sessionOne;

    private String sessionTwo;

    /**
     * 用户数量
     */
    private Integer userNum = 0;



    public boolean checkEmpty() {
        return sessionOne == null || sessionTwo == null  || ClientStatus.WAIT.equals(status);
    }

}
