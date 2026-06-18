package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.jwtdto.JwtDto;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.JwtInformation;
import com.sprint.mission.discodeit.events.UserUpdatedEvent;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthService authService;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @CacheEvict(cacheNames = "users", allEntries = true)
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    // Authentication 객체에서 principal을 가져옴
    DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();

    // TokenProvider에서 Access, Refresh 토큰 발급 받음
    String accessToken = jwtTokenProvider.generateAccessToken(authentication);
    String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
    jwtRegistry.registerJwtInformation(
        new JwtInformation(principal.getUserDto(), accessToken, refreshToken));
    authService.saveRefreshToken(principal.getUserDto().id(), refreshToken);

    // Refresh 토큰은 쿠키에 저장
    Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(14 * 24 * 60 * 60);

    // 응답에 쿠키 삽입하여 브라우저가 keep 하게 둠
    response.addCookie(refreshCookie);
    // 응답은 OK
    response.setStatus(HttpServletResponse.SC_OK);
    // 컨텐츠 타입은 JSON_VALUE
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    // 응답 body에 들어갈 JwtDto 설정 및 쓰기
    JwtDto body = new JwtDto(principal.getUserDto(), accessToken);
    eventPublisher.publishEvent(new UserUpdatedEvent(withOnline(principal.getUserDto(), true)));
    // 액세스 토큰은 응답 Body에 포함
    objectMapper.writeValue(response.getWriter(), body);
  }

  private UserDto withOnline(UserDto userDto, boolean online) {
    return new UserDto(
        userDto.id(),
        userDto.username(),
        userDto.email(),
        userDto.profile(),
        online,
        userDto.role()
    );
  }
}
