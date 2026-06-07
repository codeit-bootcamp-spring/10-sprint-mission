package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    // 1) Refresh Token 쿠키 -> 서버 측 무효화
    // Authentication이 없을 수 있으므로 refreshToken 쿠키로 세션 식별 (authentication에서 인증 정보 꺼내지 않음)
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          // 여러 쿠키 중 Refresh Token 쿠키만 필터링
          .filter(c -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
          .findFirst()
          .ifPresent(c -> {
            try {
              // Refresh Token에서 사용자 정보 추출
              UserDto userDto = jwtTokenProvider.extractUserDtoFromRefreshToken(c.getValue());
              // Registry에서 해당 userId의 모든 토큰 정보 제거
              jwtRegistry.invalidateJwtInformationByUserId(userDto.id());
            } catch (Exception ignored) {
              // 토큰이 이미 만료되었거나 위변조된 경우에도 로그아웃은 정상 진행
              // 어차피 유효하지 않은 토큰이므로 Registry 제거가 불필요 -> 무시
            }
          });
    }

    // 2) 브라우저의 Refresh Token 쿠키 삭제
    // 같은 이름의 쿠키를 MaxAge = 0으로 덮어쓰면 브라우저가 즉시 해당 쿠키를 삭제하는 원리
    Cookie cookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0); // 브라우저에서 즉시 삭제
    response.addCookie(cookie);
  }
}
