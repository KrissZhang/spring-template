package com.self.common.utils;

/**
 * 脱敏
 */
public class DesensitizedUtils {

    private DesensitizedUtils(){
    }

    /**
     * 对字符串进行脱敏操作
     *
     * @param origin          原始字符串
     * @param prefixNoMaskLen 前置不需要打码的长度
     * @param suffixNoMaskLen 后置不需要打码的长度
     * @param maskStr         打码字符
     * @return 脱敏字符
     */
    public static String desValue(String origin, int prefixNoMaskLen, int suffixNoMaskLen, String maskStr) {
        if (origin == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = origin.length(); i < n; i++) {
            if (i < prefixNoMaskLen) {
                sb.append(origin.charAt(i));
                continue;
            }

            if (i > (n - suffixNoMaskLen - 1)) {
                sb.append(origin.charAt(i));
                continue;
            }

            sb.append(maskStr);
        }

        return sb.toString();
    }

    /**
     * 【中文名称】只显示第一个汉字
     *
     * @param chineseName 名称
     * @return 脱敏字符
     */
    public static String chineseName(String chineseName) {
        if (chineseName == null) {
            return null;
        }

        return desValue(chineseName, 1, 0, "*");
    }

    /**
     * 【身份证号码】显示前4位, 后2位
     *
     * @param idCard 身份证号码
     * @return 脱敏字符
     */
    public static String idCardNum(String idCard) {
        return desValue(idCard, 4, 2, "*");
    }

    /**
     * 【手机号码】显示前3位，后4位
     *
     * @param mobilePhone 手机号码
     * @return 脱敏字符
     */
    public static String mobilePhone(String mobilePhone) {
        return desValue(mobilePhone, 3, 4, "*");
    }

}
