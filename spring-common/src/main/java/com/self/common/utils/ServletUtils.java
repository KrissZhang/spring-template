package com.self.common.utils;

import com.self.common.enums.TerminalTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 客户端工具类
 */
public class ServletUtils {

    private ServletUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(ServletUtils.class);

    /**
     * 定义移动端所有可能的请求类型
     */
    private static final String[] AGENT = {"Android", "iPhone", "iPod", "iPad", "Windows Phone", "MQQBrowser"};

    /**
     * 获取参数
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取参数
     */
    public static String getParameter(String name, String defaultValue) {
        return ConvertUtils.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取参数
     */
    public static Integer getParameterToInt(String name) {
        return ConvertUtils.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return ConvertUtils.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 获取ServletRequestAttributes
     */
    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string 待渲染的字符串
     */
    public static String renderString(HttpServletResponse response, String string) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            logger.error("页面渲染异常", e);
        }

        return null;
    }

    public static String getTerminalType(){
        Device curDevice = DeviceUtils.getCurrentDevice(getRequest());
        if(curDevice.isTablet()){
            return TerminalTypeEnum.TABLET.getValue();
        }else if(curDevice.isMobile()){
            return TerminalTypeEnum.MOBILE.getValue();
        }else{
            return TerminalTypeEnum.WEB.getValue();
        }
    }

}
