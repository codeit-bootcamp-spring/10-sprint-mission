package com.sprint.mission.discodeit.auth.handler;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;


  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    // 1. 요청 쿠키에서 REFRESH_TOKEN을 찾아 무효화 로직 수행
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
          .findFirst()
          .ifPresent(cookie -> {
            String refreshToken = cookie.getValue();

            // 토큰이 유효하고, 실제 레지스트리에 활성화된 토큰인 경우에만 ID를 추출하여 Registry에서 삭제
            if (jwtTokenProvider.validateToken(refreshToken) && jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
              String username = jwtTokenProvider.getUsername(refreshToken);
              DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

              jwtRegistry.invalidateJwtInformationByUserId(userDetails.getUserDto().id());
            }
          });
    }

    // 2. 브라우저 쿠키 삭제 (MaxAge=0)
    Cookie refreshCookie = new Cookie("REFRESH_TOKEN", "");
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(false);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(0);
    response.addCookie(refreshCookie);

    // 3. 응답 상태 코드 설정
    response.setStatus(HttpStatus.NO_CONTENT.value());
  }
}
