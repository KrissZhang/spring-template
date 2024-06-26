package com.self.common.jwt;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(description = "认证详情")
@Data
public class JWTInfo implements Serializable {

    private static final long serialVersionUID = -2192965577950942743L;

    @Schema(name = "用户id", description = "用户id")
    private Long userId;

    @Schema(name = "用户名", description = "用户名")
    private String userName;

    @Schema(name = "密码", description = "密码")
    @JSONField(serialize = false)
    @JsonIgnore
    private transient String password;

    @Schema(name = "用户真实名称", description = "用户真实名称")
    private String realName;

    @Schema(name = "密码过期日期", description = "密码过期日期")
    private Date expireDate;

    @Schema(name = "是否删除", description = "是否删除")
    private Long isDeleted;

}
