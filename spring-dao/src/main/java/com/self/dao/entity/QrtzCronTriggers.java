package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * 
 * @TableName qrtz_cron_triggers
 */
@TableName(value ="qrtz_cron_triggers")
@Data
public class QrtzCronTriggers implements Serializable {
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
    private String cronExpression;

    /**
     * 
     */
    private String timeZoneId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}