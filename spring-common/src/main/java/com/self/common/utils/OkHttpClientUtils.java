package com.self.common.utils;

import com.self.common.exception.HttpException;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.Serializable;
import java.util.Objects;

/**
 * OkHttpClient
 */
public class OkHttpClientUtils implements Serializable {

    private static volatile OkHttpClient okHttpClient = null;

    private OkHttpClientUtils(){
        if(Objects.nonNull(okHttpClient)){
            throw new HttpException("获取 okHttpClient 失败");
        }
    }

    public static OkHttpClient getInstance(){
        if(okHttpClient == null){
            synchronized (OkHttpClient.class){
                if(okHttpClient == null){
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
                }
            }
        }

        return okHttpClient;
    }

    protected Object readResolve(){
        return getInstance();
    }

}
