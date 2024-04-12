package com.self.dao.config;

import com.self.common.service.EncryptService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字段加密处理类
 */
public class EncryptTypeHandler<T> extends BaseTypeHandler<T> {

    @Autowired
    private EncryptService encryptService;

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, T t, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, encryptService.encrypt((String) t));
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String columnValue = resultSet.getString(s);
        return StringUtils.isBlank(columnValue) ? (T) columnValue : (T) encryptService.decrypt(columnValue);
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String columnValue = resultSet.getString(i);
        return StringUtils.isBlank(columnValue) ? (T) columnValue : (T) encryptService.decrypt(columnValue);
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String columnValue = callableStatement.getString(i);
        return StringUtils.isBlank(columnValue) ? (T) columnValue : (T) encryptService.decrypt(columnValue);
    }

}
