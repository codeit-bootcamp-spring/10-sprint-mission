package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

// WebSocket 인증 인터셉터
// 브라우저 WebSocket handshake는 일반 REST처럼 Authorization 헤더를 마음대로 넣기 어려움
// -> /ws handshake는 열어두고, STOMP CONNECT 프레임에서 토큰 검증
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    // STOMP 메시지의 헤더에 접근하기 위한 accessor
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null || accessor.getCommand() == null) {
      return message;
    }

    // WebSocket 연결 직후 클라이언트가 보내는 CONNECT 프레임에서만 인증 처리
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String accessToken = resolveAccessToken(accessor);

      if (!StringUtils.hasText(accessToken)
          || !jwtTokenProvider.validateToken(accessToken)
          || !jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {
        throw new AuthenticationCredentialsNotFoundException("유효하지 않은 WebSocket 인증 토큰입니다.");
      }

      // JWT subject에 저장된 userId 추출
      UUID userId = UUID.fromString(jwtTokenProvider.getSubjectFromToken(accessToken));

      // 기존 HTTP 인증과 동일한 방식으로 UserDetails 조회
      UserDetails userDetails = discodeitUserDetailsService.loadUserById(userId);

      // WebSocket 세션에서 사용할 Authentication 객체 생성
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );

      // 이후 @MessageMapping 메서드에서 Principal로 꺼낼 수 있도록 저장
      accessor.setUser(authentication);
    }

    return message;
  }

  private String resolveAccessToken(StompHeaderAccessor accessor) {

    String bearerToken = accessor.getFirstNativeHeader("Authorization");

    // 소문자 header 보조 처리
    if (!StringUtils.hasText(bearerToken)) {
      bearerToken = accessor.getFirstNativeHeader("authorization");
    }

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }

    return null;
  }
}
