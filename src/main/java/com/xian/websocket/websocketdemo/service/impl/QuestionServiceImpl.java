package com.xian.websocket.websocketdemo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xian.websocket.websocketdemo.dao.QuestionDao;
import com.xian.websocket.websocketdemo.model.QuestionModel;
import com.xian.websocket.websocketdemo.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * @author xian
 * @description
 * @createTime 2021/10/16 19:23
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionDao, QuestionModel> implements QuestionService {

    @Override
    public List<QuestionModel> listRandomQuestion(Integer number) {
        // 随机题目数量
        Page test = new Page(1,number);
        baseMapper.selectPage(test,null );
        // 总页数
        long pages = test.getPages();
        int l = new Random().nextInt((int) pages);
        Page page = new Page(l,number);
        baseMapper.selectPage(page,null);
        return page.getRecords();
    }

    @Override
    public int addQuestionList(List<QuestionModel> list) {
        saveBatch(list);
        return 0;
    }
}
