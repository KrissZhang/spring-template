package com.self.security.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * UUID
 */
public class UUIDUtils {

    public static String getShortUUID(){
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }

}
