package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.JwtDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    // 1. 인증된 사용자 정보 가져오기 (UserDetailsService에서 반환한 객체)
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    String username = userDetails.getUsername();

    // 권한 정보 가져오기
    String role = authentication.getAuthorities().iterator().next().getAuthority();

    // 2. JwtTokenProvider를 이용해 토큰 생성
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", role);
    String accessToken = jwtTokenProvider.generateAccessToken(claims, username);
    String refreshToken = jwtTokenProvider.generateRefreshToken(username);

    // 3. JwtRegistry에 등록
    JwtInformation jwtInformation = JwtInformation.builder()
        .userDto(userDetails.getUserDto())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
    jwtRegistry.registerJwtInformation(jwtInformation);

    // 4. 응답(Response) 세팅
    JwtDto jwtDto = new JwtDto(
        userDetails.getUserDto(),
        accessToken
    );
    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));


    // Refresh Token 쿠키 설정
    Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/");
    cookie.setMaxAge(7 * 24 * 60 * 60);
    response.addCookie(cookie);
  }
}
