package com.xian.websocket.websocketdemo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xian.websocket.websocketdemo.model.QuestionModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xian
 * @description
 * @createTime 2021/10/16 19:22
 */
@Mapper
public interface QuestionDao extends BaseMapper<QuestionModel> {
}
