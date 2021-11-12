package com.xian.websocket.websocketdemo;

import com.xian.websocket.websocketdemo.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class WebsocketDemoApplicationTests {

    @Resource
    private QuestionService service;

    @Test
    void contextLoads() {
        service.listRandomQuestion(5);
    }

}
