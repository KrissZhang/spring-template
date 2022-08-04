package com.self.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 字典数据表
 * @TableName sys_dict_data
 */
@TableName(value ="sys_dict_data")
@Data
public class SysDictData implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 字典类型编码
     */
    private String dictTypeCode;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 字典排序
     */
    private Integer dictSort;

    /**
     * 状态，1-启用，0-停用
     */
    private Byte status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人id
     */
    private Integer updateUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}