package com.sprint.mission.discodeit.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.jwt.dto.AccessTokenClaims;
import com.sprint.mission.discodeit.auth.jwt.dto.JwtDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    //사용자 목록 세션 캐시 무효화
    Cache userListCache = cacheManager.getCache("userListCache");
    if (userListCache != null) {
      userListCache.evict("with_session");
    }

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    // 리프레시 토큰
    String refreshToken = jwtTokenProvider.generateRefreshToken(
        userDetails.getUserDto().id().toString());
    Cookie refreshTokenCookie = jwtTokenProvider.getRefreshTokenCookie(refreshToken);
    response.addCookie(refreshTokenCookie);

    // 액세스 토큰
    AccessTokenClaims accessTokenClaims = new AccessTokenClaims(userDetails.getUserDto().role());
    String accessToken = jwtTokenProvider.generateAccessToken(
        userDetails.getUserDto().id().toString(), accessTokenClaims);
    JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), accessToken);

    // JwtRegistry
    JwtInformation jwtInformation = new JwtInformation(userDetails.getUserDto(), accessToken,
        refreshToken);
    jwtRegistry.registerJwtInformation(jwtInformation);

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println(objectMapper.writeValueAsString(jwtDto));
  }
}
