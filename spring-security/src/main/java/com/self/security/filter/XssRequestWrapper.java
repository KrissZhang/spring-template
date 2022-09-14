package com.self.security.filter;

import cn.hutool.http.HtmlUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Xss过滤
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (!StringUtils.isEmpty(value)) {
            value = HtmlUtil.filter(value);
        }

        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] valueArr = super.getParameterValues(name);
        if (Objects.nonNull(valueArr)) {
            for (int i = 0; i < valueArr.length; i++) {
                String value = valueArr[i];
                if (!StringUtils.isEmpty(value)) {
                    value = HtmlUtil.filter(value);
                }

                valueArr[i] = value;
            }
        }

        return valueArr;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> paramMap = super.getParameterMap();
        Map<String, String[]> filterMap = new LinkedHashMap<>();

        if (Objects.nonNull(paramMap)) {
            for (String key : paramMap.keySet()) {
                String[] valueArr = paramMap.get(key);

                for (int i = 0; i < valueArr.length; i++) {
                    String value = valueArr[i];
                    if (!StringUtils.isEmpty(value)) {
                        value = HtmlUtil.filter(value);
                    }

                    valueArr[i] = value;
                }

                filterMap.put(key, valueArr);
            }
        }

        return filterMap;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (!StringUtils.isEmpty(value)) {
            value = HtmlUtil.filter(value);
        }

        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!isJsonRequest()) {
            return super.getInputStream();
        }

        String json = IOUtils.toString(super.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.isEmpty(json)) {
            return super.getInputStream();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> filterMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                filterMap.put(entry.getKey(), HtmlUtil.filter(value.toString()));
            }
        }
        json = objectMapper.writeValueAsString(filterMap);

        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        final ByteArrayInputStream bis = new ByteArrayInputStream(jsonBytes);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public int available() throws IOException {
                return jsonBytes.length;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                //ignore
            }

            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };
    }

    /**
     * 是否是Json请求
     */
    public boolean isJsonRequest() {
        String header = super.getHeader(HttpHeaders.CONTENT_TYPE);
        return StringUtils.startsWithIgnoreCase(header, MediaType.APPLICATION_JSON_VALUE);
    }

}
