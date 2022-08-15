package com.xian.websocket.websocketdemo.controller;

import com.xian.websocket.websocketdemo.model.QuestionModel;
import com.xian.websocket.websocketdemo.service.QuestionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xian
 * @description
 * @createTime 2022/8/15 9:49
 */
@RestController
public class QuestionController {

    @Resource
    private QuestionService questionService;


    @RequestMapping("/addList")
    public String addList(List<QuestionModel> list) {
        questionService.addQuestionList(list);
        return "";
    }
}
