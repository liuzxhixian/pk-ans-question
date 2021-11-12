package com.xian.websocket.websocketdemo.handle;

import com.mysql.cj.util.StringUtils;
import com.xian.websocket.websocketdemo.config.WebSessionManager;
import com.xian.websocket.websocketdemo.constant.RobotName;
import com.xian.websocket.websocketdemo.model.PkRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * @author xian
 * @description
 * @createTime 2021/10/24 19:05
 */
@Slf4j
@Component
public class RoomCheckJob {

    @Scheduled(cron = "0/10 * * * * *")
    public void run() {
        log.info("检查房间情况{}",WebSessionManager.rooms);
        Iterator<PkRoom> iterator = WebSessionManager.rooms.iterator();
        // 当房间列表存在10个以上时，开启空闲房间清除
        while (iterator.hasNext() && WebSessionManager.rooms.size() > RobotName.ROOM_NUM_CLEAR) {
            PkRoom next = iterator.next();
            if (StringUtils.isNullOrEmpty(next.getSessionOne()) && StringUtils.isNullOrEmpty(next.getSessionTwo())) {
                log.info("清除空闲房间");
                iterator.remove();
            }
        }
    }

}
