package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.auth.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final UserService userService;
  private final JwtRegistry jwtRegistry;

  @Override
  public JwtDto refresh(String refreshToken, HttpServletResponse response) {

    Map<String, Object> claims =
        jwtTokenProvider.getClaims(refreshToken);

    String email = (String) claims.get("sub");

    if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      throw new DiscodeitException(ErrorCode.REVOKED_TOKEN);
    }

    RefreshToken savedRefreshToken =
        refreshTokenService.findByToken(refreshToken);

    refreshTokenService.validate(savedRefreshToken);

    UserDto userDto =
        userService.findByEmail(email);

    List<String> roles =
        List.of("ROLE_" + userDto.getRole().name());

    Map<String, Object> accessClaims =
        new HashMap<>();

    accessClaims.put("username", email);
    accessClaims.put("roles", roles);
    accessClaims.put("userId", userDto.getId().toString());

    String newAccessToken =
        jwtTokenProvider.generateAccessToken(
            accessClaims,
            email
        );

    String newRefreshToken =
        jwtTokenProvider.generateRefreshToken(email);

    refreshTokenService.rotate(
        savedRefreshToken.getUserId(),
        newRefreshToken
    );

    Cookie refreshTokenCookie =
        new Cookie("REFRESH_TOKEN", newRefreshToken);

    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false);
    refreshTokenCookie.setAttribute("SameSite", "Strict");
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(
        jwtTokenProvider.getRefreshTokenExpirationMinutes()
            * 60
    );

    response.addCookie(refreshTokenCookie);

    return JwtDto.builder()
        .userDto(userDto)
        .accessToken(newAccessToken)
        .build();
  }
}
