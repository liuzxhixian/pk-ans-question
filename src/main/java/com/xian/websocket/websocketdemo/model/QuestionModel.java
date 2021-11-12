package com.xian.websocket.websocketdemo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xian
 * @description
 * @createTime 2021/10/22 16:15
 */
@Data
@Accessors(chain = true)
@TableName("pk_question")
public class QuestionModel implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @TableField("type")
    private Integer type;

    @TableField("score")
    private Integer score;

    @TableField("answer")
    private String answer;

    @TableField("true_answer")
    private String trueAnswer;

    @TableField("question")
    private String question;

    @TableField(value = "gmt_create",fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = "gmt_modify",fill = FieldFill.INSERT_UPDATE)
    private Date gmtModify;


}
