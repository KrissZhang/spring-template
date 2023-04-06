package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户信息表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户真实名称
     */
    private String realName;

    /**
     * 手机号码
     */
    private String phoneNum;

    /**
     * 是否逻辑删除，0-未删除，时间戳-已删除
     */
    @TableLogic(value = "0", delval = "REPLACE(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)), '.', '')")
    private Byte isDeleted;

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