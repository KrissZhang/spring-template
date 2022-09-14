package com.self.common.utils;

import cn.hutool.core.util.EscapeUtil;
import com.self.common.constants.CommonConstants;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取ip方法
 */
public class IpUtils {

    private IpUtils() {
    }

    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return CommonConstants.STR_UNKNOWN;
        }

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || CommonConstants.STR_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || CommonConstants.STR_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }

        if (ip == null || ip.length() == 0 || CommonConstants.STR_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || CommonConstants.STR_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }


        if (ip == null || ip.length() == 0 || CommonConstants.STR_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : EscapeUtil.escape(ip);
    }

    /**
     * 将IPv4地址转换成字节
     * @param text IPv4地址
     * @return byte 字节
     */
    public static byte[] textToNumericFormatV4(String text) {
        if (text.length() == 0) {
            return new byte[0];
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);

        try {
            long l;
            int i;

            switch (elements.length) {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L)) {
                        return new byte[0];
                    }

                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);

                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L)) {
                        return new byte[0];
                    }

                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L)) {
                        return new byte[0];
                    }

                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);

                    break;
                case 3:
                    for (i = 0; i < 2; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return new byte[0];
                        }

                        bytes[i] = (byte) (int) (l & 0xFF);
                    }

                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L)) {
                        return new byte[0];
                    }

                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);

                    break;
                case 4:
                    for (i = 0; i < 4; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return new byte[0];
                        }

                        bytes[i] = (byte) (int) (l & 0xFF);
                    }

                    break;
                default:
                    return new byte[0];
            }
        } catch (NumberFormatException e) {
            return new byte[0];
        }

        return bytes;
    }

    public static String getHostIp() {
        String hostIp = CommonConstants.STR_LOCAL_IP;

        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            //ignore
        }

        return hostIp;
    }

    public static String getHostName() {
        String hostName = "未知";

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            //ignore
        }

        return hostName;
    }

}
