package com.sprint.mission.discodeit.auth.handler;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.auth.jwt.JwtCookieManager;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.event.UserEvents;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * JWT 기반 로그아웃을 처리하는 핸들러입니다.
 * 레지스트리에서 세션을 무효화하고 클라이언트의 쿠키를 삭제합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtCookieManager jwtCookieManager;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    // 1. 요청 쿠키에서 리프레시 토큰 추출 및 무효화
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
          .findFirst()
          .ifPresent(cookie -> {
            String refreshToken = cookie.getValue();

            // 유효한 토큰인 경우 레지스트리에서 즉시 삭제
            if (jwtTokenProvider.validateToken(refreshToken)) {
              jwtRegistry.invalidateJwtInformationByRefreshToken(refreshToken)
                  .ifPresent(userId -> eventPublisher.publishEvent(new UserEvents.OnlineStatusChanged(userId, false)));
            }
          });
    }

    // 2. 전용 매니저를 통해 쿠키 삭제
    jwtCookieManager.deleteRefreshTokenCookie(response);

    response.setStatus(HttpStatus.NO_CONTENT.value());
  }
}
