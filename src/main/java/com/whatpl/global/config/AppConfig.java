package com.whatpl.global.config;

import com.whatpl.global.cache.CacheOperator;
import com.whatpl.global.cache.RedisOperator;
import com.whatpl.global.upload.FileUploader;
import com.whatpl.global.upload.S3Uploader;
import io.awspring.cloud.s3.S3Template;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class AppConfig {

    @Bean
    public CacheOperator cacheOperator(RedisTemplate<String, Object> redisTemplate) {
        return new RedisOperator(redisTemplate);
    }

    @Bean
    public FileUploader fileUploader(S3Template s3Template) {
        return new S3Uploader(s3Template);
    }
}
