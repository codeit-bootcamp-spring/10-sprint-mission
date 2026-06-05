package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
/// Sprint Cache 어노테이션 활성화
@EnableCaching
public class CacheConfig {

    /// Redis 캐시의 공통 설정을 만드는 Bean
    /// Redis는 데이터를 문자열/바이트 형태로 저장하므로, Java 객체를 Redis에 넣기 위해 직렬화 방식이 필요하다.
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
        /// Redis용 따로 복사.
        /// ObjectMapper는 Java객체 <-> JSON 해주는 역할.
        /// 직렬화: Java객체 -> JSON
        ObjectMapper redisObjectMapper = objectMapper.copy();

        /// Redis는 JSON 문자열만 저장한다.
        /// 그냥 JSON을 저장하면 Redis입장에서는 뭔지 몰라서 클래스 정보를 같이 저장하게 만든다.
        /// "@class": "com.discodeit.dto.UserDto" 이런식으로.
        /**
         {
         "id": "123",
         "username": "eunbi"
         }
         ======================================
         변경후
         {
         "@class": "com.discodeit.user.dto.UserDto",
         "id": "123",
         "username": "eunbi"
         }
         이런식으로 @class가 추가된다.
         **/
        /// Redis가 다시 조회할때 ObjectMapper 확인하고 class가 UserDto네? 하면서 UserDto를 복원한다.
        redisObjectMapper.activateDefaultTyping(

                /// 어떤 타입 정보를 허용할지 검사하는 객체
                /// Jackson은 보안때문에 JSON안에 클래스 이름이 있으면 위험하다고 생각.
                /// Redis 내부에서 쓰는거니까 허용해준다는 설정.
                LaissezFaireSubTypeValidator.instance,

                /// JSON만 저장하면 역직렬화할때 List<UserDot>인지 UserDto인지 모를 수 있다.
                /// 그래서 JSON안에 타입 정보를 포함한다.
                ObjectMapper.DefaultTyping.EVERYTHING,

                /// 타입정보를 넣는 방법
                /// 필드하나를 넣어서 타입정보 나타내기.
                JsonTypeInfo.As.PROPERTY
        );

        return RedisCacheConfiguration.defaultCacheConfig()
                /// Redis는 key(String) - value(byte[]) 구조로 돼있다.
                /// GenericJackson2JsonRedisSerializer를 사용해서 직렬호,역직렬화
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
                        )
                )
                /// Redis key 앞에 문자열을 붙인다.
                /// 여러서비스를 같이쓰면 충돌위험이 있어서 prefix적용
                .prefixCacheNameWith("discodeit:")

                /// TTL
                .entryTtl(Duration.ofSeconds(600))
                /// null 결과는 저장하지 않는다.
                .disableCachingNullValues();
    }

//    @Bean
//    public CacheManager caffeineCacheManager() {
//        /// users(사용자 목록 조회) L1 캐시사용
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager("users");
//        cacheManager.setCaffeine(Caffeine.newBuilder()
//                .maximumSize(100)     // 최대 캐시 수 제한
//                /// 마지막 접근시점부터 10분후 만료
//                .expireAfterAccess(10, TimeUnit.MINUTES)
//
//                /// 캐시가 통계 정보를 기록하게 켜는 옵션
//                /// 캐시 hit수
//                /// 캐시 miss수
//                /// 등
//                .recordStats()); // TTL(만료시간) 설정
//        return cacheManager;
//    }

    @Bean
    public CacheManager redisCacheManager(
            /// Redis 연결 정보.
            /// yml에 설정한 내용
            RedisConnectionFactory redisConnectionFactory,
            RedisCacheConfiguration redisCacheConfiguration
    ) {
        /// Redis 기반 CacheManager 생성
        /// CacheManager -> RedisConnectionFactory -> localhost:6379 Redis 연결
        return RedisCacheManager.builder(redisConnectionFactory)

                /// 위에서 만든 기본설정
                /// value -> JSON  직렬화
                /// prefix -> discodeit:
                /// TTL -> 600초
                /// null 저장 x
                .cacheDefaults(redisCacheConfiguration)
                /// 특정 캐시이름에 설정 적용.
                .withCacheConfiguration("users", redisCacheConfiguration)
                .withCacheConfiguration("userChannels", redisCacheConfiguration)
                .withCacheConfiguration("userNotifications", redisCacheConfiguration)
                .enableStatistics()
                .build();
    }


}
