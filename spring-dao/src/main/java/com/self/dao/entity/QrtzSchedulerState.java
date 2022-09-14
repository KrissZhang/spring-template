package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * 
 * @TableName qrtz_scheduler_state
 */
@TableName(value ="qrtz_scheduler_state")
@Data
public class QrtzSchedulerState implements Serializable {
    /**
     * 
     */
    @MppMultiId
    private String schedName;

    /**
     * 
     */
    @MppMultiId
    private String instanceName;

    /**
     * 
     */
    private Long lastCheckinTime;

    /**
     * 
     */
    private Long checkinInterval;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}