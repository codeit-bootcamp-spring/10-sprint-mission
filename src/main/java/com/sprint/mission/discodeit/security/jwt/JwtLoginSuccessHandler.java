package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtProperties jwtProperties;
  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {
    // 인증된 사용자 정보 꺼내기
    UserDto userDto = ((DiscodeitUserDetails) authentication.getPrincipal()).getUserDto();

    // 토큰 생성
    String accessToken = jwtTokenProvider.generateAccessToken(userDto);
    String refreshToken = jwtTokenProvider.generateRefreshToken(userDto);

    // registry 등록
    jwtRegistry.registerJwtInformation(new JwtInformation(userDto, accessToken, refreshToken));

    // 리프레시 토큰을 HttpOnly 쿠키에 담기
    Cookie cookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (jwtProperties.refreshTokenExpiry() / 1000)); // '초' 단위로 변경
    response.addCookie(cookie);

    // Response Body에 AccessToken 담기
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), new JwtDto(userDto, accessToken)); // JSON 직렬화

    // 로그인으로 online 상태가 바뀌므로 사용자 목록 캐시 무효화
    Cache userCache = cacheManager.getCache("users");
    if (userCache != null) {
      userCache.clear(); // 캐시에 저장된 모든 항목 비우기
    }

  }
}
