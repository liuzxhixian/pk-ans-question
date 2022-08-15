package com.xian.websocket.websocketdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xian.websocket.websocketdemo.model.QuestionModel;

import java.util.List;

/**
 * @author xian
 * @description
 * @createTime 2021/10/16 19:23
 */
public interface QuestionService extends IService<QuestionModel> {


    /**
     * 随机获取题目
     * @param number 题目数量
     * @return
     */
    List<QuestionModel> listRandomQuestion(Integer number);


    int addQuestionList(List<QuestionModel> list);
}
