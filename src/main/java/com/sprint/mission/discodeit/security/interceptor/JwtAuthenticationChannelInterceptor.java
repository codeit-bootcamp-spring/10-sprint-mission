package com.sprint.mission.discodeit.security.interceptor;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.exception.security.InvalidJwtTokenException;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.text.ParseException;

/**
 * STOMP 메시지가 서버 내부 채널로 들어올 때 실행되는 인터셉터
 *
 * Controller보다 먼저 실행되기에 아래와 같은 공통 처리를 넣기 좋다.
 * - CONNECT 시점에 인증 토큰 검사
 * - SEND 메시지의 헤더/본문 검사
 * - SUBSCRIBE 권한 검사
 * - DISCONNECT 로그 기록
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    private final DiscodeitUserDetailsService discodeitUserDetailsService;

    // CONNECT 프레임일 때 엑세스 토큰을 검증하고, 인증된 사용자 정보를 STOMP 메시지의 simpUser 헤더에 저장
    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class
        );

        if (accessor == null) {
            return message;
        }

        // CONNECT 프레임일 경우 검증 시작
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticate(accessor);
        }

        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {
        // `CONNECT` 프레임의 헤더에서 Access Token 추출
        String accessToken = resolveAccessToken(accessor);

        // 검증된 사용자 정보 + 권한으로 인증 객체 생성
        UsernamePasswordAuthenticationToken authentication = createAuthentication(accessToken);

        // 인증된 사용자 정보를 STOMP 메시지의 simpUser 헤더에 저장
        accessor.setUser(authentication);
    }

    // `CONNECT` 프레임의 헤더에서 Access Token 추출
    private String resolveAccessToken(StompHeaderAccessor accessor) {
        // `CONNECT` 프레임의 헤더 중 Authorization 헤더 추출
        String authorizationHeader = accessor.getFirstNativeHeader("Authorization");


        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")
        ) {
            throw new InvalidJwtTokenException();
        }

        // "Bearer "을 제외한 나머지(Access Token)를 가져옴
        String accessToken = authorizationHeader.substring("Bearer ".length());

        if (accessToken.isBlank()) {
            throw new BadCredentialsException("Access Token이 비어있습니다.");
        }

        return accessToken;
    }

    // 검증된 사용자 정보 + 권한으로 인증 객체 생성
    private UsernamePasswordAuthenticationToken createAuthentication(String accessToken) {
        try {
            // JWT 검증, Access Token 여부 확인 후 claims 반환
            // 예외 발생 시, 실패 원인 추가를 위해 "try...catch"문 안에 포함
            JWTClaimsSet jwtClaimsSet = jwtTokenProvider.getAndValidateAccessToken(accessToken);

            // Access Token이 Active인지 확인
            validateActiveAccessToken(accessToken);

            // claims에서 username 조회
            String username = jwtClaimsSet.getStringClaim("username");

            // claims로 사용자 조회
            DiscodeitUserDetails userDetails =
                    (DiscodeitUserDetails) discodeitUserDetailsService.loadUserByUsername(username);

            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
        } catch (InvalidJwtTokenException | UsernameNotFoundException | ParseException e) {
            // 토큰 검증이나 사용자 조회에 실패할 경우
            throw new BadCredentialsException("유효하지 않음 Access Token입니다.", e);
        }
    }

    // Access Token이 Active인지 확인
    private void validateActiveAccessToken(String accessToken) {
        if (!jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {
            throw new BadCredentialsException("Active Access Token이 아닙니다.");
        }
    }
}
