package com.self.biz.service;

import com.self.common.exception.ParamException;
import com.self.common.utils.MinioUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Resource
    private MinioUtils minioUtils;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        return minioUtils.uploadFile(multipartFile);
    }

    public void downloadFile(HttpServletResponse response, String fileId, String fileName){
        if(StringUtils.isBlank(fileId)){
            throw new ParamException("文件id不能为空");
        }

        ServletOutputStream os = null;
        try {
            byte[] bytes = minioUtils.downloadFile(fileId);
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            if (Objects.nonNull(bytes)) {
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
            }
        } catch (IOException e) {
            logger.error("下载文件输出流异常：", e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

}
