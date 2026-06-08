package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
    ObjectMapper redisObjectMapper = objectMapper.copy();

    // Redis에는 객체 타입 정보까지 함께 저장해야 다시 꺼낼 때 DTO/List 타입으로 복원할 수 있습니다.
    redisObjectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance, DefaultTyping.EVERYTHING, As.PROPERTY);

    return RedisCacheConfiguration.defaultCacheConfig()
        // 캐시 key prefix입니다. Redis에서 discodeit:users::SimpleKey 같은 형태로 저장됩니다.
        .prefixCacheNameWith("discodeit:")
        // 캐시 유지 시간입니다.
        .entryTtl(Duration.ofSeconds(600))
        // null 결과는 캐싱하지 않습니다.
        .disableCachingNullValues()
        // value를 JSON 형태로 직렬화합니다.
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(redisObjectMapper)));
  }
}
