package com.farmporject.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * File upload interface for frontend el-upload component:
 * POST /file/upload/order
 * formData: { file: MultipartFile }
 * Response: { flag: true, data: "filename.ext" }
 */
@RestController
@RequestMapping("/file/upload")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private static final String BASE_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "order";

    @PostMapping("/order")
    public ResponseEntity<?> uploadOrderImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> resp = new HashMap<>();
        
        logger.info("收到文件上传请求，文件名: {}, 大小: {} bytes", 
            file.getOriginalFilename(), file.getSize());
        
        if (file == null || file.isEmpty()) {
            logger.warn("上传的文件为�?");
            resp.put("flag", false);
            resp.put("message", "文件不能为空");
            return ResponseEntity.badRequest().body(resp);
        }
        
        // 检查文件大小（10MB限制�?
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            logger.warn("文件大小超过限制: {} bytes", file.getSize());
            resp.put("flag", false);
            resp.put("message", "文件大小不能超过10MB");
            return ResponseEntity.badRequest().body(resp);
        }
        
        try {
            String original = file.getOriginalFilename();
            logger.info("原始文件�?: {}", original);
            
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path dir = Paths.get(BASE_DIR);
            
            logger.info("上传目录: {}", dir.toAbsolutePath());
            
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                logger.info("创建上传目录: {}", dir.toAbsolutePath());
            }
            
            Path target = dir.resolve(StringUtils.cleanPath(filename));
            logger.info("目标文件路径: {}", target.toAbsolutePath());
            
            file.transferTo(target.toFile());
            
            logger.info("文件上传成功: {}", filename);

            resp.put("flag", true);
            resp.put("data", filename);
            return ResponseEntity.ok(resp);
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            resp.put("flag", false);
            resp.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        } catch (Exception e) {
            logger.error("文件上传出现未知错误", e);
            resp.put("flag", false);
            resp.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}