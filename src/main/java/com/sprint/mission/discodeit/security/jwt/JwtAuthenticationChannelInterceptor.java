package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {
  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      return message;
    }

    // WebSocket 연결 시작 프레임에만 JWT 인증
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String accessToken = resolveAccessToken(accessor);

      if (!StringUtils.hasText(accessToken)) {
        throw new MessagingException("Authorization header is missing");
      }
      if (!tokenProvider.validateAccessToken(accessToken)
          || !jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {
        throw new MessagingException("Invalid access token");
      }

      String username = tokenProvider.getUsernameFromToken(accessToken);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      // WebSocket 메시지 흐름에서는 SecurityContext가 아니라 accessor에 인증 정보 저장
      accessor.setUser(authentication);

      log.debug("WebSocket CONNECT authenticated: username={}", username);
    }

    return message;
  }

  private String resolveAccessToken(StompHeaderAccessor accessor) {
    String authorization = accessor.getFirstNativeHeader("Authorization");

    if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
      return authorization.substring(7);
    }

    return null;
  }
}
