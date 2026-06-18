package com.sprint.mission.discodeit.config.websocket;

import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = getToken(accessor.getFirstNativeHeader("Authorization"));
      if (token == null) {
        log.debug("[WS] 토큰 누락");
        throw new MessageDeliveryException(message, "인증 토큰이 누락되었습니다.");
      }

      try {
        Map<String, Object> claims = jwtTokenProvider.verifyAndGetClaims(token);

        if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
          throw new MessageDeliveryException(message, "유효하지 않은 토큰입니다.");
        }

        String username = claims.get("sub").toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

        log.debug("[WS] JWT 인증 성공");
        accessor.setUser(authentication);
      } catch (InvalidTokenException e) {
        log.debug("[WS] 토큰 검증 실패");
        throw new MessageDeliveryException(message, "토큰 검증이 실패했습니다.");
      } catch (Exception e) {
        log.debug("[WS] JWT 인증 실패");
        throw new MessageDeliveryException(message, "JWT 인증이 실패했습니다.");
      }
    }

    return message;
  }

  private String getToken(String authorization) {
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      return null;
    }

    return authorization.replaceAll("Bearer ", "");
  }
}
