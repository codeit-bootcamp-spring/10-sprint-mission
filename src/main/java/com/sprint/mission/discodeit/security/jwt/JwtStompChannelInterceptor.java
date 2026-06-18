package com.sprint.mission.discodeit.security.jwt;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtStompChannelInterceptor implements ChannelInterceptor {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
        message,
        StompHeaderAccessor.class
    );
    if (accessor == null) {
      return message;
    }
    StompCommand command = accessor.getCommand();

    if (StompCommand.CONNECT.equals(command)) {
      accessor.setUser(authenticate(accessor));
      return message;
    }

    if ((StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command))
        && accessor.getUser() == null) {
      throw new AccessDeniedException("Authentication is required for WebSocket messages.");
    }

    return message;
  }

  private UsernamePasswordAuthenticationToken authenticate(StompHeaderAccessor accessor) {
    String token = resolveToken(accessor);
    if (!StringUtils.hasText(token)
        || !tokenProvider.validateAccessToken(token)
        || !jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
      throw new AccessDeniedException("Invalid WebSocket access token.");
    }

    String username = tokenProvider.getUsernameFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );
    log.debug("Set WebSocket authentication for user: {}", username);
    return authentication;
  }

  private String resolveToken(StompHeaderAccessor accessor) {
    List<String> authorizationHeaders = accessor.getNativeHeader(AUTHORIZATION_HEADER);
    if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
      return null;
    }

    String bearerToken = authorizationHeaders.get(0);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }
    return null;
  }
}
