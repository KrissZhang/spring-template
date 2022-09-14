package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * 
 * @TableName qrtz_triggers
 */
@TableName(value ="qrtz_triggers")
@Data
public class QrtzTriggers implements Serializable {
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
    private String jobName;

    /**
     * 
     */
    private String jobGroup;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private Long nextFireTime;

    /**
     * 
     */
    private Long prevFireTime;

    /**
     * 
     */
    private Integer priority;

    /**
     * 
     */
    private String triggerState;

    /**
     * 
     */
    private String triggerType;

    /**
     * 
     */
    private Long startTime;

    /**
     * 
     */
    private Long endTime;

    /**
     * 
     */
    private String calendarName;

    /**
     * 
     */
    private Short misfireInstr;

    /**
     * 
     */
    private byte[] jobData;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}