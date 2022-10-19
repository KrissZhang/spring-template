package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 操作日志信息表
 * @TableName oper_log
 */
@TableName(value ="oper_log")
@Data
public class OperLog implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 终端类别
     */
    private String terminalType;

    /**
     * 标题
     */
    private String title;

    /**
     * 功能类型
     */
    private Byte businessType;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求方式
     */
    private String reqMethod;

    /**
     * 操作人id
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 请求ip
     */
    private String reqIp;

    /**
     * 请求url
     */
    private String reqUrl;

    /**
     * 请求参数
     */
    private String reqParam;

    /**
     * 返回参数
     */
    private String respResult;

    /**
     * 操作状态
     */
    private Byte status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private Date operateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}