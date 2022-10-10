package com.self.common.utils.http;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BaseHttpRequest {

    private static final Logger logger = LoggerFactory.getLogger(BaseHttpRequest.class);

    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, Object> queryParams = new HashMap<>();
    private final Map<String, Object> bodyParams = new HashMap<>();
    private final Method method;
    private final String url;

    public BaseHttpRequest(String url, Method method) {
        this.url = url;
        this.method = method;
    }

    protected void header(String name, String value) {
        headers.put(name, value);
    }

    protected String removeHeader(String name) {
        return headers.remove(name);
    }

    protected Optional<String> header(String name) {
        return Optional.ofNullable(headers.get(name));
    }

    protected void queryParam(String name, Object value) {
        queryParams.put(name, value);
    }

    protected Optional<Object> queryParam(String name) {
        return Optional.ofNullable(queryParams.get(name));
    }

    protected final void bodyParam(String name, Object value) {
        bodyParams.put(name, value);
    }

    protected final void fileParam(String filePath) {
        bodyParams.put("filePath", filePath);
    }

    protected Optional<Object> bodyParam(String name) {
        return Optional.ofNullable(bodyParams.get(name));
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public Map<String, Object> getBodyParams() {
        return bodyParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getUrl() {
        return url;
    }

    Request.Builder requestBuilder() {
        return method.builder(bodyParams)
                .tag(BaseHttpRequest.class, this)
                .cacheControl(new CacheControl.Builder().noCache().build());
    }

    public enum Method {
        /**
         * put method
         */
        PUT {
            @Override
            public Request.Builder builder(Map<String, Object> bodyParams) {
                String params = JSONObject.toJSONString(bodyParams);
                return new Request.Builder().put(RequestBody.create(params, JSON));
            }
        },
        /**
         * delete method
         */
        DELETE {
            @Override
            public Request.Builder builder(Map<String, Object> bodyParams) {
                String params = JSONObject.toJSONString(bodyParams);
                return new Request.Builder().delete(RequestBody.create(params, JSON));
            }
        },
        /**
         * post method
         */
        POST {
            @Override
            public Request.Builder builder(Map<String, Object> bodyParams) {
                String params = JSONObject.toJSONString(bodyParams);
                return new Request.Builder().post(RequestBody.create(params, JSON));
            }
        },
        FORM {
            @Override
            public Request.Builder builder(Map<String, Object> bodyParams) {
                okhttp3.FormBody.Builder formEncodingBuilder = new okhttp3.FormBody.Builder();
                if (bodyParams != null) {
                    for (Map.Entry<String, Object> entry : bodyParams.entrySet()) {
                        formEncodingBuilder.add(entry.getKey(), entry.getValue().toString());
                    }
                }
                return new Request.Builder().post(formEncodingBuilder.build());
            }
        },
        /**
         * post file method
         */
        FILE {
            @Override
            public Request.Builder builder(Map<String, Object> bodyParams) {
                if (bodyParams.get("filePath") == null) {
                    throw new IllegalArgumentException("filePath is null");
                }
                File file = new File(bodyParams.get("filePath").toString());
                MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(),
                                RequestBody.create(file, MediaType.parse(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE)));
                // 参数
                for (Map.Entry<String, Object> entry : bodyParams.entrySet()) {
                    multipartBody.addFormDataPart(entry.getKey(), entry.getValue().toString());
                }
                return new Request.Builder().post(multipartBody.build());
            }
        },
        /**
         * get method
         */
        GET {
            @Override
            public Request.Builder builder(Map<String, Object> bodyParams) {
                return new Request.Builder().get();
            }
        };

        public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public abstract Request.Builder builder(Map<String, Object> bodyParams);
    }

}
