package com.sprint.mission.discodeit.interceptor;

import com.sprint.mission.discodeit.config.JwtTokenProvider;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);
    if (accessor == null) {
      return message;
    }

    // 연결 요청이 들어왔다면
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = resolveToken(accessor); // 헤더로부터 토큰 값 추출하고

      // 토큰 값 검증이 성공 && jwtRegistry 내에 Active한 토큰 값이 존재한다면
      // 해당 토큰을 바탕으로 Authentication 객체를 가져오고
      // STOMP의 세션의 사용자로 설정한다.
      if (token != null && jwtTokenProvider.validateAccessToken(token)
          && jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        accessor.setUser(authentication);
      }
    }
    return message;
  }

  // STOMP의 StompHeaderAccessor에서 토큰 값을 추출하고 반환하는 메서드
  private String resolveToken(StompHeaderAccessor accessor) {
    String authorization = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
      return authorization.substring(BEARER_PREFIX.length()); // 토큰 앞 Bearer 를 빼고 토큰값을 반환
    }
    return null;
  }
}
