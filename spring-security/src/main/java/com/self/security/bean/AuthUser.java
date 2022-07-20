package com.self.security.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser implements UserDetails {

    private static final long serialVersionUID = 837713892357707791L;

    private String tokenId;

    private Integer userId;

    private String terminalType;

    private Long authTimeMs;

    private Long expiresTimeMs;

    private JWTInfo jwtInfo;

    private List<String> authorityList = Lists.newArrayList();

    public AuthUser(JWTInfo jwtInfo, List<String> authorityList) {
        this.userId = jwtInfo.getUserId();
        this.jwtInfo = jwtInfo;
        this.authorityList = authorityList;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(CollectionUtils.isEmpty(authorityList)){
            return Lists.newArrayList();
        }

        return ImmutableSet.copyOf(authorityList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
    }

    @JSONField(serialize = false)
    @JsonIgnore
    @Override
    public String getPassword() {
        return jwtInfo.getPassword();
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return jwtInfo.getUserName();
    }

    /**
     * 账号是否未过期
     */
    @JSONField(serialize = false)
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账号是否解锁
     */
    @JSONField(serialize = false)
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 账号凭证(密码)是否未过期
     */
    @JSONField(serialize = false)
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账号是否可用
     */
    @JSONField(serialize = false)
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

}
