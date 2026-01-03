package com.farmporject.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final String ORDER_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "order" + File.separator;
    private static final String AVATAR_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatar" + File.separator;
    private static final String LOANS_FILES_DIR = System.getProperty("user.dir") + File.separator + "loans_files" + File.separator;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/order/**")
                .addResourceLocations("file:" + ORDER_DIR);

        // 兼容前端使用的 /file/{filename} 访问图片的方式
        // 同时支持 order 和 avatar 目录
        registry.addResourceHandler("/file/**")
                .addResourceLocations("file:" + ORDER_DIR, "file:" + AVATAR_DIR);

        // 贷款文件下载
        registry.addResourceHandler("/loans_files/**")
                .addResourceLocations("file:" + LOANS_FILES_DIR);
    }
}

