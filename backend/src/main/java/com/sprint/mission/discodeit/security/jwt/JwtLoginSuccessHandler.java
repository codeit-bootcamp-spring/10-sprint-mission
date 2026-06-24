package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.event.UserEvent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      try {
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        // Set refresh token in HttpOnly cookie
        Cookie refreshCookie = tokenProvider.genereateRefreshTokenCookie(refreshToken);
        response.addCookie(refreshCookie);

        JwtDto jwtDto = new JwtDto(
            userDetails.getUserDto(),
            accessToken
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

        jwtRegistry.registerJwtInformation(
            new JwtInformation(
                userDetails.getUserDto(),
                accessToken,
                refreshToken
            )
        );

        // 로그인 후 online 상태가 반영된 UserDto를 Kafka를 통해 모든 인스턴스에 브로드캐스트
        userRepository.findById(userDetails.getId())
            .map(userMapper::toDto)
            .ifPresent(dto -> {
              try {
                String payload = objectMapper.writeValueAsString(
                    new UserEvent(UserEvent.Action.UPDATED, dto));
                kafkaTemplate.send("discodeit.UserEvent", payload);
              } catch (IOException e) {
                log.error("로그인 UserEvent Kafka 발행 실패: userId={}", userDetails.getId(), e);
              }
            });

        log.info("JWT access and refresh tokens issued for user: {}", userDetails.getUsername());

      } catch (JOSEException e) {
        log.error("Failed to generate JWT token for user: {}", userDetails.getUsername(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = new ErrorResponse(
            new RuntimeException("Token generation failed"),
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
      }
    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      ErrorResponse errorResponse = new ErrorResponse(
          new RuntimeException("Authentication failed: Invalid user details"),
          HttpServletResponse.SC_UNAUTHORIZED
      );
      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
  }

}