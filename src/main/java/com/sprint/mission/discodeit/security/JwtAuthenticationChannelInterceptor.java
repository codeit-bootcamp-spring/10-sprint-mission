package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/// WebSocket연결시점에서 JWT 인증을 수행하는 인터셉터
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;

    /// CONNECT 들어옴 -> preSend()로 검증
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        /// STOMP 헤더 추출
        /// message안에 STOMP 정보 들어있다.
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        /// CONNECT가 아니면 무시한다.
        /// CONNECT: 인증 수행
        /// SUBSCRIBE: 패스
        /// SEND: 패스
        if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) {
            return message;
        }

        /// 순수 JWT
        String token = resolveToken(accessor);
        /// JWT 인증
        Authentication authentication = authenticate(token);
        /// STOMP 세션에 Principal로 저장.
        accessor.setUser(authentication);

        /// 현재 CONNECT 메시지를 처리하는 스레드의 SecurityContext 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        return message;

    }

    private String resolveToken(StompHeaderAccessor accessor) {
        /// STOMP Native Header에서 Authorization 조회
        String authorization = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);

        /// authorization이 존재? || Bearer로 시작하는지? 검증
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            throw new BadCredentialsException("Authorization Bearer token is required");
        }

        /// Bearer 제거: 순사 JWT만 남긴다.
        /// ex) Bearer abc.def.xyz -> abc.def.xyz
        String token = authorization.substring(BEARER_PREFIX.length()).trim();

        if (!StringUtils.hasText(token)) {
            throw new BadCredentialsException("JWT token is empty");
        }

        return token;
    }

    private Authentication authenticate(String token) {
        /// JWT 자체 검증
        /// 서명정상인지, 만료안됐는지, 변조 안됐는지.
        boolean validToken = jwtTokenProvider.validateAccessToken(token)
                /// 서버에 등록된 JWT인지 확인
                && jwtRegistry.hasActiveJwtInformationByAccessToken(token);

        if (!validToken) {
            throw new BadCredentialsException("Invalid or expired JWT token");
        }

        /// username 추출
        String username = jwtTokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        /// Spring Security 인증객체 생성
        /// 이 WebSocket 사용자는 인증된 USER라는 객체
        return UsernamePasswordAuthenticationToken.authenticated(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }


}
