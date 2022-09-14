package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * 
 * @TableName qrtz_simprop_triggers
 */
@TableName(value ="qrtz_simprop_triggers")
@Data
public class QrtzSimpropTriggers implements Serializable {
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
    private String strProp1;

    /**
     * 
     */
    private String strProp2;

    /**
     * 
     */
    private String strProp3;

    /**
     * 
     */
    private Integer intProp1;

    /**
     * 
     */
    private Integer intProp2;

    /**
     * 
     */
    private Long longProp1;

    /**
     * 
     */
    private Long longProp2;

    /**
     * 
     */
    private BigDecimal decProp1;

    /**
     * 
     */
    private BigDecimal decProp2;

    /**
     * 
     */
    private String boolProp1;

    /**
     * 
     */
    private String boolProp2;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}