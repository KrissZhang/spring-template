package com.self.common.utils.http;

import com.self.common.exception.HttpException;
import com.self.common.lambda.HttpRequestAction;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtils.class);

    private static volatile OkHttpUtils instance;

    private OkHttpClient client;

    private OkHttpClient httpsClient;

    private static final long TIMEOUT = 30;

    private OkHttpUtils() {
        client = new OkHttpClient.Builder().
                connectTimeout(TIMEOUT, TimeUnit.SECONDS).
                readTimeout(TIMEOUT, TimeUnit.SECONDS).
                writeTimeout(TIMEOUT,TimeUnit.SECONDS).
                addInterceptor(new RequestHttpInterceptor()).
                build();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            httpsClient = new OkHttpClient.Builder().
                    sslSocketFactory(sc.getSocketFactory(), new TrustAnyTrustManager()).
                    hostnameVerifier(new TrustAnyHostnameVerifier()).
                    connectTimeout(TIMEOUT, TimeUnit.SECONDS).
                    readTimeout(TIMEOUT, TimeUnit.SECONDS).
                    writeTimeout(TIMEOUT,TimeUnit.SECONDS).
                    addInterceptor(new RequestHttpInterceptor()).
                    build();
        } catch (Exception e) {
            logger.error("create https client exception", e);
        }
    }

    public static OkHttpUtils getInstance() {
        if (instance == null) {
            synchronized (OkHttpUtils.class) {
                if (instance == null) {
                    instance = new OkHttpUtils();
                }
            }
        }
        return instance;
    }

    public String sendRequest(BaseHttpRequest request) {
        try {
            Call call = createNewCall(request);
            Response response = call.execute();
            return resolveResponse(response);
        } catch (IOException e) {
            throw new HttpException("http call exception", e);
        }
    }

    public Response callResponse(BaseHttpRequest request) {
        try {
            Call call = createNewCall(request);
            return call.execute();
        } catch (IOException e) {
            throw new HttpException("http call exception", e);
        }
    }

    /**
     * 获取下载文件流
     * @param request
     * @return
     */
    public InputStream sendStreamRequest(BaseHttpRequest request) {
        try {
            Call call = createNewCall(request);
            Response response = call.execute();
            return response.body().byteStream();
        } catch (IOException e) {
            throw new HttpException("http call exception", e);
        }
    }

    /**
     * 获取下载文件流
     * @param request
     * @return
     */
    public byte[] sendBytesRequest(BaseHttpRequest request) {
        try {
            Call call = createNewCall(request);
            Response response = call.execute();
            return response.body().bytes();
        } catch (IOException e) {
            throw new HttpException("http call exception", e);
        }
    }

    public void sendRequestAsync(BaseHttpRequest request, final HttpRequestAction action) {
        Call call = createNewCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                action.failed(request, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = resolveResponse(response);
                    action.success(request, result);
                } catch (Exception e) {
                    action.failed(request, e.getMessage());
                }
            }
        });
    }

    private Call createNewCall(BaseHttpRequest request){
        Request request1 = buildRequest(request);
        if (request1.isHttps()) {
            return httpsClient.newCall(request1);
        } else {
            return client.newCall(request1);
        }
    }

    private Request buildRequest(BaseHttpRequest request) {
        return request.requestBuilder().url(request.getUrl()).build();
    }

    private String resolveResponse(Response response) throws IOException {
        try (Response finalResponse = response) {
            if (!finalResponse.isSuccessful()) {
                throw new HttpException(String.format("http call exception : %d", finalResponse.code()));
            }
            String result = new String(Objects.requireNonNull(finalResponse.body()).bytes(), StandardCharsets.UTF_8);
            return result;
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
