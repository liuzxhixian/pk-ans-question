package com.xian.websocket.websocketdemo.config;

import com.mysql.cj.util.StringUtils;
import com.xian.websocket.websocketdemo.constant.ClientStatus;
import com.xian.websocket.websocketdemo.model.PkRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author xian
 * @description
 * @createTime 2021/10/24 11:12
 */
@Slf4j
public class WebSessionManager {

    public static ConcurrentHashMap<String, WebSocketSession>  SESSION_POOL = new ConcurrentHashMap<>(64);
    public static CopyOnWriteArrayList<PkRoom> rooms = new CopyOnWriteArrayList<>();

    /**
     * 用户名和session绑定
     * @param key
     * @param session
     */
    public static void add(String key, WebSocketSession session) {
        SESSION_POOL.put(key, session);
        log.info("[{}]用户和Session[{}]绑定",key,session.getId());
        log.info("[{}]用户进入PK等待",key);
    }

    /**
     * 用户名和session解绑
     * @param key
     * @return
     */
    public static WebSocketSession remove(String key) {
        log.info("[{}]用户离开房间");
        WebSocketSession session = SESSION_POOL.remove(key);
        log.info("[{}]用户和Session[{}]取消绑定",key,session.getId());
        return session;
    }

    /**
     * 删除并同步关闭连接
     *
     * @param key
     */
    public static void removeAndClose(String key) {
        WebSocketSession session = remove(key);
        if (session != null) {
            try {
                // 关闭连接
                session.close();
                log.info("Session[{}]关闭",key,session.getId());
            } catch (IOException e) {
                // todo: 关闭出现异常处理
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过session把绑定的用户给删除以及踢出房间
     * @param session
     */
    public static void removeByData(WebSocketSession session) {
        Iterator<Map.Entry<String, WebSocketSession>> iterator = SESSION_POOL.entrySet().iterator();
        String username = null;
        while (iterator.hasNext()) {
            Map.Entry<String, WebSocketSession> next = iterator.next();
            if (next.getValue() == session) {
                username = next.getKey();
                iterator.remove();
            }
        }
        // 找到username的房间，把他踢出去
        if (!StringUtils.isNullOrEmpty(username)) {
            for (PkRoom room : rooms) {
                if (username.equals(room.getSessionOne())) {
                    room.setSessionOne(null);
                    room.setUserNum(room.getUserNum() - 1);
                    room.setStatus(ClientStatus.WAIT);
                } else if (username.equals(room.getSessionTwo())) {
                    room.setSessionTwo(null);
                    room.setUserNum(room.getUserNum() - 1);
                    room.setStatus(ClientStatus.WAIT);
                }
            }
        }
    }

    /**
     * 通过昵称获得 session
     *
     * @param key
     * @return
     */
    public static WebSocketSession get(String key) {
        // 获得 session
        if(StringUtils.isNullOrEmpty(key)) {
            return null;
        }
        return SESSION_POOL.get(key);
    }

    /**
     * 返回房间号，有值则表示已进入房间
     * @return 返回房间号
     */
    public static String checkRoom(String username) {
        if (StringUtils.isNullOrEmpty(username)) {
            return null;
        }
        for (PkRoom room : rooms) {
            if (username.equals(room.getSessionOne()) || username.equals(room.getSessionTwo())) {
                return room.getRoomNo();
            }
        }
        return null;
    }

    /**
     * 进入房间，找到一个剩余一个位置的的房间，如果没有，新建一个房间
     * @param username
     * @return
     */
    public static synchronized PkRoom setInRoom(String username) {
        for (PkRoom room : rooms) {
            if (room.checkEmpty()) {
                // 房间不为空，并且处于等待状态
                if (ClientStatus.WAIT.equals(room.getStatus())) {
                    if (StringUtils.isNullOrEmpty(room.getSessionOne())) {
                        room.setSessionOne(username);
                        room.setUserNum(room.getUserNum() + 1);
                    } else  if (StringUtils.isNullOrEmpty(room.getSessionTwo())){
                        room.setSessionTwo(username);
                        room.setUserNum(room.getUserNum() + 1);
                    } else {
                        break;
                    }
                    if (room.getUserNum() == 2) {
                        room.setStatus(ClientStatus.READY);
                    }
                    return room;
                }
            }
        }
        String roomNo = UUID.randomUUID().toString();
        PkRoom pkRoom = new PkRoom().setRoomNo(roomNo).setSessionOne(username).setStatus(ClientStatus.WAIT).setUserNum(1);
        rooms.add(pkRoom);
        return pkRoom;
    }


    /**
     * 获取房间信息
     * @param roomNo
     * @return
     */
    public static PkRoom getRoom(String roomNo) {
        for (PkRoom room : rooms) {
            if (roomNo.equals(room.getRoomNo())) {
                return room;
            }
        }
        return null;
    }

    /**
     * 离开房间
     * @param username
     */
    public static void leaveRoom(String username) {
        if (StringUtils.isNullOrEmpty(username)) {
            return;
        }
        for (PkRoom room : rooms) {
            if (username.equals(room.getSessionOne())) {
                room.setSessionOne(null);
                room.setUserNum(room.getUserNum()-1);
                room.setStatus(ClientStatus.WAIT);
            } else if (username.equals(room.getSessionTwo())) {
                room.setSessionTwo(null);
                room.setUserNum(room.getUserNum()-1);
                room.setStatus(ClientStatus.WAIT);
            }
        }
    }


}
