package com.delivery.management.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel工具类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Component
public class ExcelUtil {

    /**
     * 导出Excel
     * 
     * @param response HTTP响应
     * @param fileName 文件名
     * @param sheetName 工作表名称
     * @param data 数据列表
     * @param clazz 数据类
     */
    public static <T> void export(HttpServletResponse response, String fileName, String sheetName, 
                                  List<T> data, Class<T> clazz) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            
            // 文件名URL编码
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            // 写入Excel
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet(sheetName)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(data);

            log.info("Excel导出成功：{}", fileName);
        } catch (IOException e) {
            log.error("Excel导出失败：{}", e.getMessage(), e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }
}

