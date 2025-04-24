package com.self.common.utils;

import com.self.common.config.MinioClientConfig;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

@Component
@ConditionalOnBean(MinioClientConfig.class)
public class MinioUtils {

    private static final Logger logger = LoggerFactory.getLogger(MinioUtils.class);

    @Autowired
    private MinioClientConfig minioClientConfig;

    @Autowired
    private MinioClient minioClient;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file){
        // 后缀名
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        // 文件名
        String fileName = RandomStringUtils.randomAlphanumeric(32) + type;

        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(minioClientConfig.getMinioBucketName()).object(fileName).stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            // 文件名称相同会覆盖
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            logger.error("上传文件失败：", e);
            return null;
        }

        return fileName;
    }

    /**
     * 下载文件
     */
    public byte[] downloadFile(String fileName) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(minioClientConfig.getMinioBucketName()).object(fileName).build();

        byte[] bytes = null;
        try (GetObjectResponse response = minioClient.getObject(objectArgs)) {
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                bytes = os.toByteArray();
            }
        } catch (Exception e) {
            logger.error("下载文件失败：", e);
            return null;
        }

        return bytes;
    }

}
