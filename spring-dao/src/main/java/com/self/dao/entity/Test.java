package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 测试信息表
 * @TableName test
 */
@TableName(value ="test")
@Data
public class Test implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 是否启用
     */
    private Byte enable;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}