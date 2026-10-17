package com.niit.shopmall.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Redis 缓存工具（带开关与降级）
 */
@Slf4j
@Component
public class RedisUtil {

    @Value("${mall.redis.enabled}")
    private boolean enabled;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 写入缓存
     */
    public void set(String key, Object value, long seconds) {
        if (!enabled) {
            return;
        }
        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), Duration.ofSeconds(seconds));
        } catch (Exception e) {
            log.warn("Redis 写入失败，已降级：{}", e.getMessage());
        }
    }

    /**
     * 读取缓存，按类型反序列化，未命中或失败返回 null
     */
    public <T> T get(String key, Class<T> clazz) {
        if (!enabled) {
            return null;
        }
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("Redis 读取失败，已降级：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 读取缓存，按泛型类型反序列化（用于 List 等）
     */
    public <T> T get(String key, TypeReference<T> typeReference) {
        if (!enabled) {
            return null;
        }
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.warn("Redis 读取失败，已降级：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        if (!enabled) {
            return;
        }
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis 删除失败，已降级：{}", e.getMessage());
        }
    }
}
