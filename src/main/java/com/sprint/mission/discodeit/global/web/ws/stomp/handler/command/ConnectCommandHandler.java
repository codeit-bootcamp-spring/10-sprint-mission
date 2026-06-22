package com.sprint.mission.discodeit.global.web.ws.stomp.handler.command;

import com.sprint.mission.discodeit.auth.domain.provider.JwtRegistry;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationErrorCode;
import com.sprint.mission.discodeit.global.security.exception.JwtAuthenticationException;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.global.web.ws.stomp.handler.StompCommandHandler;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
class ConnectCommandHandler implements StompCommandHandler {

  private final JwtTokenProvider provider;
  private final JwtRegistry registry;

  @Override
  public boolean canHandle(StompCommand command) {
    return StompCommand.CONNECT.equals(command);
  }

  @Override
  public void handle(StompHeaderAccessor accessor) {
    String token = parseToken(accessor);
    if (!StringUtils.hasText(token)) {
      throw new JwtAuthenticationException(JwtAuthenticationErrorCode.TOKEN_NOT_FOUND);
    }
    provider.validate(token);
    String deviceId = accessor.getFirstNativeHeader("X-Device-Id");
    Authentication authentication = provider.getAuthentication(token);
    if (!registry.isActiveSession(UUID.fromString(authentication.getName()), deviceId)) {
      throw new JwtAuthenticationException(JwtAuthenticationErrorCode.ACTIVE_DEVICE_CHANGED);
    }
    accessor.setUser(authentication);
  }

  private String parseToken(StompHeaderAccessor accessor) {
    String header = accessor.getFirstNativeHeader("Authorization");
    if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }
}
