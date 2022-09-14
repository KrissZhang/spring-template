package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * 
 * @TableName qrtz_job_details
 */
@TableName(value ="qrtz_job_details")
@Data
public class QrtzJobDetails implements Serializable {
    /**
     * 
     */
    @MppMultiId
    private String schedName;

    /**
     * 
     */
    @MppMultiId
    private String jobName;

    /**
     * 
     */
    @MppMultiId
    private String jobGroup;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private String jobClassName;

    /**
     * 
     */
    private String isDurable;

    /**
     * 
     */
    private String isNonconcurrent;

    /**
     * 
     */
    private String isUpdateData;

    /**
     * 
     */
    private String requestsRecovery;

    /**
     * 
     */
    private byte[] jobData;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}