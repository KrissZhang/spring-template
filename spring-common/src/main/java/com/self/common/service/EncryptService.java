package com.self.common.service;

/**
 * 加密方法
 */
public interface EncryptService {

    /**
     * 加密算法
     */
    String encrypt(String content);

    /**
     * 解密算法
     */
    String decrypt(String content);

}
