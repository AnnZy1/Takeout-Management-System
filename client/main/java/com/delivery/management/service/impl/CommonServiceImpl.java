package com.delivery.management.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 通用服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.domain}")
    private String domain;

    /**
     * 上传文件到OSS
     */
    @Override
    public String upload(MultipartFile file) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 验证文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过5MB");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!extension.matches("(?i)\\.(jpg|jpeg|png|gif)")) {
            throw new BusinessException("只支持jpg、jpeg、png、gif格式的图片");
        }

        // 生成文件路径
        String folder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString() + extension;
        String objectName = folder + "/" + fileName;

        try {
            // 创建OSS客户端
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 上传文件
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(bucketName, objectName, inputStream);

            // 关闭OSS客户端
            ossClient.shutdown();

            // 返回文件URL
            String url = domain + "/" + objectName;
            log.info("文件上传成功：{}", url);
            return url;
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }
}

