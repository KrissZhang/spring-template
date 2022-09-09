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
 * @TableName qrtz_blob_triggers
 */
@TableName(value ="qrtz_blob_triggers")
@Data
public class QrtzBlobTriggers implements Serializable {
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
    private byte[] blobData;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}