package com.self.security.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class JWTInfo implements Serializable {

    private static final long serialVersionUID = -2192965577950942743L;

    private Integer userId;

    private String userName;

    @JSONField(serialize = false)
    @JsonIgnore
    private transient String password;

    private String realName;

}
