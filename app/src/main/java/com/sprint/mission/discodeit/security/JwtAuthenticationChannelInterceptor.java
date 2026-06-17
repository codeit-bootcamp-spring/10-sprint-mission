package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.DiscodeitUnauthorizedException;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authorization = accessor.getFirstNativeHeader("Authorization");

      if (authorization == null || !authorization.startsWith("Bearer ")) {
        throw new DiscodeitUnauthorizedException();
      }
      String token = authorization.substring(7);

      if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
        throw new DiscodeitUnauthorizedException();
      }
      if (jwtTokenProvider.isExpired(token)) {
        throw new DiscodeitUnauthorizedException();
      }
      Map<String, Object> claims = jwtTokenProvider.getClaims(token);
      UUID userId = UUID.fromString(claims.get("sub").toString());
      String username = claims.get("username").toString();
      String email = claims.get("email").toString();
      Role role = Role.fromString(claims.get("roles").toString());

      UserDto userDto = new UserDto(
          userId,
          username,
          email,
          null,
          true,
          role
      );

      DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, null);

      Authentication authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities()
      );
      accessor.setUser(authentication);
    }
    return message;
  }
}
