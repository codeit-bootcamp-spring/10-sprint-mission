package com.sprint.mission.discodeit.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CacheEvictService {

    private final CacheManager cacheManager;

    // 특정 캐시 호출 및 비우기
    public void evictUserCaches(UUID userId) {
        cacheManager.getCache("notifications").evict(userId);
        cacheManager.getCache("channels").evict(userId);
        cacheManager.getCache("users").evict("allUsers");
    }
}