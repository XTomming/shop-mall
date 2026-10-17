package com.niit.shopmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC 配置：映射本地上传目录为静态资源
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${mall.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolute = new File(uploadPath).getAbsolutePath().replace("\\", "/");
        if (!absolute.endsWith("/")) {
            absolute += "/";
        }
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + absolute);
    }
}
