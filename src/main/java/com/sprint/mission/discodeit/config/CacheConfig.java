package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  @ConditionalOnProperty(
      name = "spring.cache.type",
      havingValue = "caffeine",
      matchIfMissing = true //디폴트
  )
  public CacheManager cacheManager() {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();

    caffeineCacheManager.registerCustomCache(
        "channelListCache",
        Caffeine.newBuilder()
            .maximumSize(100)
            .recordStats() // 캐시 통계
            .expireAfterAccess(600, TimeUnit.SECONDS)
            .build()
    );

    caffeineCacheManager.registerCustomCache(
        "userListCache",
        Caffeine.newBuilder()
            .maximumSize(100)
            .recordStats() // 캐시 통계
            .expireAfterAccess(600, TimeUnit.SECONDS)
            .build()
    );

    caffeineCacheManager.registerCustomCache(
        "notificationListCache",
        Caffeine.newBuilder()
            .maximumSize(100)
            .recordStats() // 캐시 통계
            .expireAfterAccess(600, TimeUnit.SECONDS)
            .build()
    );

    return caffeineCacheManager;
  }

  @Bean
  @ConditionalOnProperty(
      name = "spring.cache.type",
      havingValue = "redis"
  )
  public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
    ObjectMapper redisObjectMapper = objectMapper.copy();
    redisObjectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        DefaultTyping.EVERYTHING,
        As.PROPERTY
    );

    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
            )
        )
        .prefixCacheNameWith("discodeit:")
        .entryTtl(Duration.ofSeconds(600))
        .disableCachingNullValues();
  }

}
