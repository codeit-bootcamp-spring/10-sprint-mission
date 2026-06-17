package com.sprint.mission.discodeit.config.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

// 캐시 환경을 설정하는 클래스
@Configuration
@EnableCaching
public class CacheConfig {

    // Redis에 캐시 값을 JSON으로 저장하고 다시 Java 객체로 복원하기 위한 설정 Bean
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
        // 애플리케이션 전체 ObjectMapper 설정을 해치지 않도록 Redis 전용 복사본을 생성
        ObjectMapper redisObjectMapper = objectMapper.copy();

        // Object 타입, List<DTO> 같은 다형성 객체를 역직렬화할 수 있도록 타입 정보를 JSON에 포함시키는 로직
        // 1파라미터: 타입 정보 검증기로 Jackson 기본 relaxed validator를 사용
        // 2파라미터: 캐시 값 전체에 타입 정보를 붙여 Redis에서 깨널 때 원래 타입으로 복원되게 설정
        // 3파라미터: 타입 정보를 JSON property 형태로 저장("@class": "com.sprint.mission.discodeit.dto.UserDto")
        redisObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                DefaultTyping.EVERYTHING,
                As.PROPERTY
        );

        // Spring Redis Cache의 기본 설정을 시작점으로 사용
        return RedisCacheConfiguration.defaultCacheConfig()
                // Redis에 저장할 value의 직렬화/역직렬화 방식을 JSON 기반 serializer로 지정
                // GenericJackson2JsonRedisSerializer를 Spring Cache가 요구하는 SerializerPair 형식으로 감쌈
                // 타입 정보가 포함된 ObjectMapper를 사용해 저장 시 JSON으로 변환하고, 조회 시 Java 객체로 복원
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(
                                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
                        )
                )
                // Redis key 앞에 서비스 전용 prefix를 붙여 다른 데이터와 충돌을 피함
                .prefixCacheNameWith("discodeit:")
                // 캐시 항목의 TTL을 600초(10분)로 설정해 오래된 데이터가 자동으로 만료되게 함
                .entryTtl(Duration.ofSeconds(600))
                // null 결과는 Redis에 캐싱하지 않게 설정
                .disableCachingNullValues();
    }
}
