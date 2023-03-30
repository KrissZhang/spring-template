package com.self.biz.request;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import java.util.Map;

/**
 * 腾讯天气请求接口
 * @Auther: zp
 * @Date: 2023/3/30 - 03 - 30 - 15:02
 * @Description: com.self.biz.request
 */
public interface TxWeatherRequest {

    /**
     * 包含斜杆时必须以斜杠结尾
     */
    String BASE_URL = "https://wis.qq.com/weather/";

    @GET("common")
    Call<String> getWeatherInfo(@QueryMap Map<String, Object> map);

}
