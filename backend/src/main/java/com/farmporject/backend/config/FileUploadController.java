package com.farmporject.backend.config;

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
 * 简单文件上传接口，兼容前端 el-upload:
 * POST /file/upload/order
 * formData: { file: MultipartFile }
 * 返回: { flag: true, data: "filename.ext" }
 */
@RestController
@RequestMapping("/file/upload")
public class FileUploadController {

    private static final String BASE_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "order";

    @PostMapping("/order")
    public ResponseEntity<?> uploadOrderImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> resp = new HashMap<>();
        if (file.isEmpty()) {
            resp.put("flag", false);
            resp.put("message", "文件为空");
            return ResponseEntity.badRequest().body(resp);
        }
        try {
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path dir = Paths.get(BASE_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path target = dir.resolve(StringUtils.cleanPath(filename));
            file.transferTo(target.toFile());

            resp.put("flag", true);
            resp.put("data", filename);
            return ResponseEntity.ok(resp);
        } catch (IOException e) {
            resp.put("flag", false);
            resp.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }
}








