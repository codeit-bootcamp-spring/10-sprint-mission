package com.sprint.mission.discodeit.event.listener.cache;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.ChannelEvents;
import com.sprint.mission.discodeit.event.UserEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 도메인 이벤트를 구독하여 관련 캐시를 무효화하는 리스너입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CacheListener {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelCreated(ChannelEvents.Created event) {
        if (event.type() == ChannelType.PUBLIC) {
            evictAll("userChannelsCache");
        } else {
            evictSpecificUsers(event.participantIds());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelUpdated(ChannelEvents.Updated event) {
        if (event.type() == ChannelType.PUBLIC) {
            evictAll("userChannelsCache");
        } else {
            evictSpecificUsers(event.participantIds());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelDeleted(ChannelEvents.Deleted event) {
        if (event.type() == ChannelType.PUBLIC || event.participantIds().isEmpty()) {
            // 공개 채널이거나 참여자 정보가 없는 경우 안전하게 전체 비우기
            evictAll("userChannelsCache");
            log.info("[CacheListener] 채널 삭제(Type={})로 인해 전체 채널 캐시 무효화", event.type());
        } else {
            // 비공개 채널 참여자들만 타겟팅 삭제
            evictSpecificUsers(event.participantIds());
            log.info("[CacheListener] 비공개 채널 삭제로 인해 참여자({}) 캐시 무효화", event.participantIds().size());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserUpdated(UserEvents.Updated event) {
        evictAll("usersCache");
        evictAll("userChannelsCache"); // 참여자 정보 동기화를 위해 채널 캐시도 초기화
    }

    @EventListener
    public void handleUserOnlineStatusChanged(UserEvents.OnlineStatusChanged event) {
        evictAll("usersCache");
        evictAll("userChannelsCache"); // 상태 정보 동기화를 위해 채널 캐시도 초기화
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserChannelAccessChanged(ChannelEvents.AccessChanged event) {
        evictSpecificUsers(List.of(event.userId()));
        log.info("[CacheListener] 사용자({})의 접근 권한 변경으로 채널 캐시 무효화", event.userId());
    }

    private void evictAll(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.debug("[Cache Evicted] All entries in {}", cacheName);
        }
    }

    private void evictSpecificUsers(Collection<UUID> userIds) {
        Cache cache = cacheManager.getCache("userChannelsCache");
        if (cache != null && userIds != null) {
            userIds.forEach(cache::evict);
            log.debug("[Cache Evicted] Specific users {} in {}", userIds, "userChannelsCache");
        }
    }
}
