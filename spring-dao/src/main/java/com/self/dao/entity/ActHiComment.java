package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName act_hi_comment
 */
@TableName(value ="act_hi_comment", autoResultMap = true)
@Data
public class ActHiComment {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String type;

    /**
     * 
     */
    private Date time;

    /**
     * 
     */
    private String userId;

    /**
     * 
     */
    private String taskId;

    /**
     * 
     */
    private String procInstId;

    /**
     * 
     */
    private String action;

    /**
     * 
     */
    private String message;

    /**
     * 
     */
    private byte[] fullMsg;
}