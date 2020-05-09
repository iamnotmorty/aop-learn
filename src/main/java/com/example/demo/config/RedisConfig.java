package com.example.demo.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/7
 * Time: 11:18
 * Description: No Description
 */
//@Configuration
//@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
//        return RedisCacheManager.create(factory);
//    }
//
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(factory);
//        return redisTemplate;
//    }
}
