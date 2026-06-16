package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtCookieManager;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 로그인 성공 시 JWT를 생성하고 세션을 등록하는 핸들러입니다.
 */
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final ObjectMapper objectMapper;
  private final UserStatusService userStatusService;
  private final CacheManager cacheManager;
  private final JwtCookieManager jwtCookieManager;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    String username = userDetails.getUsername();

    String roles = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    // 1. 토큰 생성 (Access & Refresh)
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", roles);
    String accessToken = jwtTokenProvider.generateAccessToken(claims, username);
    String refreshToken = jwtTokenProvider.generateRefreshToken(username);

    // 2. 서버 측 레지스트리에 세션 정보 저장 (실시간 관리용)
    JwtInformation jwtInformation = JwtInformation.builder()
        .userDto(userDetails.getUserDto())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
    jwtRegistry.registerJwtInformation(jwtInformation);

    // 3. 사용자 상태 업데이트 및 목록 캐시 무효화 (실시간 On/Off 상태 반영)
    userStatusService.updateByUserId(userDetails.getUserDto().id(), new UserStatusDto.UpdateRequest(Instant.now()));
    evictUsersCache();

    // 4. 전용 매니저를 통해 쿠키 설정
    jwtCookieManager.addRefreshTokenCookie(response, refreshToken);
    
    // 5. JSON 응답 전송
    writeResponse(response, userDetails, accessToken);
  }

  private void writeResponse(HttpServletResponse response, DiscodeitUserDetails userDetails, String token) throws IOException {
    JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), token);
    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
  }

  private void evictUsersCache() {
    Cache cache = cacheManager.getCache("usersCache");
    if (cache != null) cache.clear();
  }
}
