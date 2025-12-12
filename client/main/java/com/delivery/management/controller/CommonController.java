package com.delivery.management.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.common.result.Result;
import com.delivery.management.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private CommonService commonService;

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
     * 上传图片
     * 
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        log.info("上传图片：{}", file.getOriginalFilename());
        String url = commonService.upload(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success("上传成功", result);
    }

    /**
     * 图片代理接口，解决浏览器ORB限制
     * 
     * @param url 图片URL
     * @param response 响应对象
     */
    @GetMapping("/image-proxy")
    public void imageProxy(@RequestParam("url") String url, HttpServletResponse response) {
        log.info("图片代理请求：{}", url);
        
        try {
            // 如果是OSS域名的图片，直接从OSS下载
            if (url.startsWith(domain)) {
                // 提取objectName
                String objectName = url.substring(domain.length() + 1);
                
                // 创建OSS客户端
                OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                
                // 获取文件输入流
                InputStream inputStream = ossClient.getObject(bucketName, objectName).getObjectContent();
                
                // 设置响应头
                response.setContentType("image/*");
                
                // 将文件内容写入响应
                ServletOutputStream outputStream = response.getOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                
                // 关闭资源
                outputStream.close();
                inputStream.close();
                ossClient.shutdown();
            } else {
                // 其他域名的图片，重定向
                response.sendRedirect(url);
            }
        } catch (Exception e) {
            log.error("图片代理失败：{}", e.getMessage(), e);
            throw new BusinessException("图片加载失败");
        }
    }
}

