package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;
  private final UserRepository userRepository;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authorizationHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String accessToken = authorizationHeader.substring("Bearer ".length());

        try {

          if (jwtTokenProvider.validateToken(accessToken)
              && jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {
            UUID userId = UUID.fromString(jwtTokenProvider.getSubjectFromToken(accessToken));
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
              throw new BadCredentialsException("토큰에 해당하는 사용자가 존재하지 않습니다.");
            }
            UserDetails userDetails = discodeitUserDetailsService.loadUserByUsername(
                user.getUsername());

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
            accessor.setUser(authentication);
          } else {
            log.error("유효하지 않거나 만료된 토큰으로 웹소캣");
            throw new BadCredentialsException("유효하지 않거나 만료된 토큰입니다.");
          }
        } catch (Exception e) {
          log.error("웹소켓 인증 처리 중 오류 발생: {}", e.getMessage(), e);
          throw new MessageDeliveryException(e.getMessage());
        }

      }
    }
    return message;
  }
}
