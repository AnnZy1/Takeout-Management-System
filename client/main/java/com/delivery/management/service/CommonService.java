package com.delivery.management.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 通用服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface CommonService {

    /**
     * 上传文件到OSS
     * 
     * @param file 文件
     * @return 文件URL
     */
    String upload(MultipartFile file);
}

