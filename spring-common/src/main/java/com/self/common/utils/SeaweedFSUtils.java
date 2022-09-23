package com.self.common.utils;

import net.anumbrella.seaweedfs.core.FileTemplate;
import net.anumbrella.seaweedfs.core.file.FileHandleStatus;
import net.anumbrella.seaweedfs.core.http.StreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class SeaweedFSUtils {

    private static final Logger logger = LoggerFactory.getLogger(SeaweedFSUtils.class);

    @Resource
    private FileTemplate fileTemplate;

    public FileHandleStatus uploadFile(String fileName, InputStream inputStream) throws IOException {
        return fileTemplate.saveFileByStream(fileName, inputStream);
    }

    public StreamResponse getFileStream(String fileId) throws IOException {
        return fileTemplate.getFileStream(fileId);
    }

    public void deleteFile(String fileId) {
        try {
            fileTemplate.deleteFile(fileId);
        } catch (Exception e) {
            logger.error("文件删除失败：", e);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file.getOriginalFilename(), file.getInputStream()).getFileId();
    }

    public String uploadFile(byte[] bytes, String fileName) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        FileHandleStatus fileHandleStatus = uploadFile(fileName, bais);
        return fileHandleStatus.getFileId();
    }

    public byte[] downloadFile(String fileId) throws IOException {
        StreamResponse fileStream = getFileStream(fileId);
        ByteArrayOutputStream baos = (ByteArrayOutputStream) fileStream.getOutputStream();
        return baos.toByteArray();
    }

}
