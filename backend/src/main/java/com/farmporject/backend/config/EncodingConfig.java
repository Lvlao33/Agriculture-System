package com.farmporject.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 字符编码配置
 * 确保所有HTTP请求和响应都使用UTF-8编码
 */
@Configuration
public class EncodingConfig {

    @Bean
    public CharacterEncodingFilter apiCharacterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }
}

