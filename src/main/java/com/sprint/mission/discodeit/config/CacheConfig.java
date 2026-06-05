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
                .maximumSize(100)     // 최대 캐시 수 제한
                /// 마지막 접근시점부터 10분후 만료
                .expireAfterAccess(10, TimeUnit.MINUTES)

                /// 캐시가 통계 정보를 기록하게 켜는 옵션
                /// 캐시 hit수
                /// 캐시 miss수
                /// 등
                .recordStats()); // TTL(만료시간) 설정
        return cacheManager;
    }
}