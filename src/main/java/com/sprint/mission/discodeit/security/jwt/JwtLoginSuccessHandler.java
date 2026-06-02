package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.JwtInformation;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;
  private final JwtRegistry jwtRegistry;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException {
    log.info("# JWT Authentication success handler");

    DiscodeitUserDetails userDetails =
        (DiscodeitUserDetails) authentication.getPrincipal();

    UserDto userDto = userDetails.getUserDto();

    Map<String, Object> claims = Map.of(
        "userId", userDto.id().toString(),
        "email", userDto.email(),
        "username", userDto.username(),
        "roles", authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList()
    );

    String accessToken = jwtTokenProvider.generateAccessToken(
        claims,
        userDto.email()
    );

    String refreshToken = jwtTokenProvider.generateRefreshToken(
        userDto.email()
    );

    jwtRegistry.registerJwtInformation(
        new JwtInformation(
            userDto.id(),
            accessToken,
            refreshToken,
            jwtTokenProvider.getExpiration(accessToken),
            jwtTokenProvider.getExpiration(refreshToken)
        )
    );

    Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(
        jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60
    );

    response.addCookie(refreshTokenCookie);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    objectMapper.writeValue(
        response.getWriter(),
        new JwtDto(userDto, accessToken)
    );
  }
}