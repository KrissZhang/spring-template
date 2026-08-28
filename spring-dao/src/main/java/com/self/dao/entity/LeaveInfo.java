package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 请假信息表
 * @TableName leave_info
 */
@TableName(value ="leave_info", autoResultMap = true)
@Data
public class LeaveInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 申请人id
     */
    private Long applicant;

    /**
     * 请假天数
     */
    private Integer days;

    /**
     * 请假原因
     */
    private String reason;

    /**
     * 状态，0-审批中，1-通过，2-驳回
     */
    private Integer status;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 是否逻辑删除，0-未删除，时间戳-已删除
     */
    @TableLogic(value = "0", delval = "REPLACE(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)), '.', '')")
    private Long isDeleted;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}