package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * 
 * @TableName qrtz_paused_trigger_grps
 */
@TableName(value ="qrtz_paused_trigger_grps")
@Data
public class QrtzPausedTriggerGrps implements Serializable {
    /**
     * 
     */
    @MppMultiId
    private String schedName;

    /**
     * 
     */
    @MppMultiId
    private String triggerGroup;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}