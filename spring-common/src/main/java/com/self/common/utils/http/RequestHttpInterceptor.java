package com.self.common.utils.http;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class RequestHttpInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestHttpInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        BaseHttpRequest tag = request.tag(BaseHttpRequest.class);

        //组装header
        Map<String, String> headers = tag.getHeaders();
        if (headers != null && headers.size() > 0) {
            Headers.Builder headerBuilder = request.headers().newBuilder();
            headers.forEach((name, value) -> {
                String parameter = null;
                if (!Objects.isNull(value)) {
                    parameter = value;
                }
                headerBuilder.add(name, parameter);
            });
            request = request.newBuilder().headers(headerBuilder.build()).build();
            logger.debug("header: {}", headers);
        }

        //组装 queryString
        Map<String, Object> queryParams = tag.getQueryParams();
        if (queryParams != null && queryParams.size() > 0) {
            HttpUrl.Builder urlBuilder = request.url().newBuilder();
            queryParams.forEach((name, value) -> {
                String parameter = null;
                if (!Objects.isNull(value)) {
                    parameter = value.toString();
                }
                urlBuilder.addQueryParameter(name, parameter);
            });
            request = request.newBuilder().url(urlBuilder.build()).build();
            logger.debug("queryParams: {}", queryParams);
        }

        return chain.proceed(request);
    }

}
