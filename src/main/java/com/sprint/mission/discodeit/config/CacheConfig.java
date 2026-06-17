package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;


@Configuration
public class CacheConfig {

  // RedisCacheManager가 이 설정을 기본 캐시 설정으로 사용
  @Bean
  public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
    // Spring은 ObjectMapper를 빈 하나만으로 관리하는데, 전역 ObjectMapper에 Redis 설정을 추가하면,
    // HTTP 응답에도 @class 정보가 노출되기 때문에 copy()를 통해서 캐시 전용 ObjectMapper를 따로 사용
    ObjectMapper redisObjectMapper = objectMapper.copy();
    // Default Typing 활성화: 직렬화된 JSON에 실제 타입 정보(@class)를 함께 기록
    // 캐시에 저장된 JSON을 다시 읽을 때 어떤 클래스로 역직렬화해야 하는지 알 수 있게 해준다.
    redisObjectMapper.activateDefaultTyping(
        // SubType 검증기: 역직렬화 허용 타입을 제한하는 보안 장치
        // LaissezFaireSubTypeValidator는 검증하지 않음(모든 타입 허용)을 의미
        // 신뢰할 수 없는 외부 입력이 Redis로 들어올 수 있는 환경에서는 역직렬화 취약점(RCE-Remote Code Execution) 위험이 있으므로 주의 필요
        LaissezFaireSubTypeValidator.instance,
        // EVERYTHING: final 타입을 포함한 거의 모든 값에 타입 정보를 부여한다.
        DefaultTyping.EVERYTHING,
        // 파일 정보를 별도 프로퍼티(@class)로 저장하는 방식
        As.PROPERTY
    );

    // Redis 캐시의 기본 설정에서 필요한 항목만 덮어쓰기
    return RedisCacheConfiguration.defaultCacheConfig()
        // 값 직렬화 방식 지정
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer( // 이 직렬화기를 읽기/쓰기 양쪽에 모두 사용
                new GenericJackson2JsonRedisSerializer(redisObjectMapper) // Jackson 기반 Json 직렬화기
            )
        )
        .prefixCacheNameWith("discodeit:") // 모든 캐시 키 앞에 이 접두사를 붙임
        .entryTtl(Duration.ofSeconds(600)) // 캐시 엔트리의 TTL을 600초로 설정
        .disableCachingNullValues(); // null 값은 캐시에 저장하지 않음
  }
}
