package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * 
 * @TableName qrtz_fired_triggers
 */
@TableName(value ="qrtz_fired_triggers")
@Data
public class QrtzFiredTriggers implements Serializable {
    /**
     * 
     */
    @MppMultiId
    private String schedName;

    /**
     * 
     */
    @MppMultiId
    private String entryId;

    /**
     * 
     */
    private String triggerName;

    /**
     * 
     */
    private String triggerGroup;

    /**
     * 
     */
    private String instanceName;

    /**
     * 
     */
    private Long firedTime;

    /**
     * 
     */
    private Long schedTime;

    /**
     * 
     */
    private Integer priority;

    /**
     * 
     */
    private String state;

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
    private String isNonconcurrent;

    /**
     * 
     */
    private String requestsRecovery;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}