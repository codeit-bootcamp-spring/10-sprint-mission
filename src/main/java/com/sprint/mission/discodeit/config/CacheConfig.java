package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("users", "userChannels", "userNotifications");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)     // 최대 캐시 수 제한
                .expireAfterWrite(10, TimeUnit.MINUTES)); // TTL(만료시간) 설정
        return cacheManager;
    }
}
