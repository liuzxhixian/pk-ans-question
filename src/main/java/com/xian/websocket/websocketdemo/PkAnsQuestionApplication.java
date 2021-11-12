package com.xian.websocket.websocketdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lzx
 */
@SpringBootApplication
@EnableScheduling
public class PkAnsQuestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PkAnsQuestionApplication.class, args);
    }

}
