package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    log.debug("로그 아웃 요청");

    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(c -> c.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
          .findFirst()
          .ifPresent(c -> {
            log.debug("로그아웃 요청에서 리프레시 토큰 확인");
            String refreshToken = c.getValue();
            try {
              if (jwtTokenProvider.validateToken(refreshToken)
                  && jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
                UUID userId = UUID.fromString(jwtTokenProvider.getSubjectFromToken(refreshToken));
                jwtRegistry.invalidateJwtInformationByUserId(userId);
                log.debug("레지스트리에서 사용자 관련 메모리 삭제: userId={}", userId);
              }
              log.debug("유효하지 않은 리프레시 토큰");
            } catch (Exception e) {
              log.warn("로그아웃 요청 토큰에서 사용자 아이디를 파싱할 수 없음");
            }
          });
    }

    Cookie cookie = jwtTokenProvider.getRefreshTokenCookie(null, 0);//즉시 만료
    response.addCookie(cookie);
    log.info("로그아웃 성공");
  }
}
