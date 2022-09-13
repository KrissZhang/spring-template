package com.self.quartz.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * Cron 表达式
 */
public class CronUtils {

    private static final String FORMAT = "ss mm HH dd MM ? yyyy";

    /**
     * 获取时间指定的 Cron表达式
     * @param date 时间
     * @return Cron表达式
     */
    public static String getCron(Date date){
        return DateFormatUtils.format(date, FORMAT);
    }

}
