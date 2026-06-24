package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.UserEvent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    // Clear refresh token cookie
    Cookie refreshTokenExpirationCookie = tokenProvider.genereateRefreshTokenExpirationCookie();
    response.addCookie(refreshTokenExpirationCookie);

    Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
        .findFirst()
        .ifPresent(cookie -> {
          String refreshToken = cookie.getValue();
          UUID userId = tokenProvider.getUserId(refreshToken);
          jwtRegistry.invalidateJwtInformationByUserId(userId);

          // 로그아웃 후 online=false 상태가 반영된 UserDto를 Kafka를 통해 모든 인스턴스에 브로드캐스트
          userRepository.findById(userId)
              .map(userMapper::toDto)
              .ifPresent(dto -> {
                try {
                  String payload = objectMapper.writeValueAsString(
                      new UserEvent(UserEvent.Action.UPDATED, dto));
                  kafkaTemplate.send("discodeit.UserEvent", payload);
                } catch (Exception e) {
                  log.error("로그아웃 UserEvent Kafka 발행 실패: userId={}", userId, e);
                }
              });
        });

    log.debug("JWT logout handler executed - refresh token cookie cleared");
  }
}