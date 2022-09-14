package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * 
 * @TableName qrtz_simple_triggers
 */
@TableName(value ="qrtz_simple_triggers")
@Data
public class QrtzSimpleTriggers implements Serializable {
    /**
     * 
     */
    @MppMultiId
    private String schedName;

    /**
     * 
     */
    @MppMultiId
    private String triggerName;

    /**
     * 
     */
    @MppMultiId
    private String triggerGroup;

    /**
     * 
     */
    private Long repeatCount;

    /**
     * 
     */
    private Long repeatInterval;

    /**
     * 
     */
    private Long timesTriggered;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}