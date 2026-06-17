package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
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

  //  <Caffeine 캐시인 경우>
//  @Bean
//  public CacheManager cacheManager() {
//    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
//
//    // 유저 정보 캐시 설정 - 자주 조회되지만 변경이 잦음
//    cacheManager.registerCustomCache("users",
//        Caffeine.newBuilder()
//            .expireAfterWrite(5, TimeUnit.MINUTES) // 5분 뒤 만료
//            .maximumSize(500) // 최대 500개
//            .recordStats() // 지표 수집 활성화
//            .build());
//
//    // 채널 목록 캐시 설정 - 상대적으로 변경이 적고 무거움
//    cacheManager.registerCustomCache("channels",
//        Caffeine.newBuilder()
//            .expireAfterWrite(30, TimeUnit.MINUTES) // 30분 뒤 만료
//            .maximumSize(100) // 최대 100개
//            .recordStats() // 지표 수집 활성화
//            .build());
//
//    // 그 외 기본 캐시들에 적용할 디폴트 설정
//    cacheManager.setCaffeine(Caffeine.newBuilder()
//        .expireAfterWrite(10, TimeUnit.MINUTES) // 10분 뒤 만료
//        .maximumSize(1000) // 최대 1000개
//        .recordStats()); // 지표 수집 활성화
//
//    return cacheManager;

  //    <Redis인 경우>
  @Bean
  public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
    ObjectMapper redisObjectMapper = objectMapper.copy();

    redisObjectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        DefaultTyping.EVERYTHING,
        As.PROPERTY
    );

    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(600))
        .disableCachingNullValues()
        .prefixCacheNameWith("discodeit:")
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
            )
        );
  }
}
