package com.farmporject.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final String ORDER_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "order" + File.separator;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/order/**")
                .addResourceLocations("file:" + ORDER_DIR);
    }
}

