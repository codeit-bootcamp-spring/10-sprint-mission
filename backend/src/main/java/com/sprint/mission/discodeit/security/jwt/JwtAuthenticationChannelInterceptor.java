package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
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
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      String bearerToken = accessor.getFirstNativeHeader("Authorization");
      String token = resolveToken(bearerToken);

      if (StringUtils.hasText(token)) {
        if (tokenProvider.validateAccessToken(token)
            && jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
          String username = tokenProvider.getUsernameFromToken(token);
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities()
              );

          accessor.setUser(authentication);
          log.debug("WebSocket CONNECT authenticated for user: {}", username);
        } else {
          log.debug("WebSocket CONNECT: invalid JWT token");
          throw new IllegalArgumentException("Invalid JWT token");
        }
      } else {
        log.debug("WebSocket CONNECT: no token provided");
        throw new IllegalArgumentException("Authorization token is required");
      }
    }

    return message;
  }

  private String resolveToken(String bearerToken) {
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
