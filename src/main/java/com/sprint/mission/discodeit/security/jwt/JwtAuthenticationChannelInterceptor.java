package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor != null
        && StompCommand.CONNECT.equals(accessor.getCommand())) {

      String authorization = accessor.getFirstNativeHeader("Authorization");

      if (authorization == null || !authorization.startsWith("Bearer ")) {
        throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
      }

      String token = authorization.substring(7);

      if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
        throw new DiscodeitException(ErrorCode.REVOKED_TOKEN);
      }

      Authentication authentication =
          jwtTokenProvider.getAuthentication(token);

      System.out.println(authentication.getAuthorities());

      accessor.setUser(authentication);
    }

    return message;
  }
}
