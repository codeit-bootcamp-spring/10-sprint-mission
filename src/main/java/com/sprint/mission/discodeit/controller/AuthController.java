package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.*;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;
  private final JwtRegistry jwtRegistry;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest request) {
    return ResponseEntity.ok(userService.updateRole(request));
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
    // 쿠키에서 REFRESH_TOKEN 추출
    String refreshToken = null;
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("REFRESH_TOKEN".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
          break;
        }
      }
    }

    // 리프레시 토큰 없거나 유효하지 않은 경우
    if (refreshToken == null || !jwtTokenProvider.validate(refreshToken)
            || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(new ErrorResponse(
                      Instant.now(),
                      "INVALID_REFRESH_TOKEN",
                      "유효하지 않은 리프레시 토큰입니다.",
                      null,
                      "InvalidRefreshTokenException",
                      HttpStatus.UNAUTHORIZED.value()
              ));
    }

    // 유저 정보 조회
    UUID userId = jwtTokenProvider.getUserId(refreshToken);
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) discodeitUserDetailsService.loadUserByUsername(
            jwtTokenProvider.getUsername(refreshToken)
    );
    UserDto userDto = userDetails.getUserDto();

    // Access Token 재발급
    String newAccessToken = jwtTokenProvider.generateAccessToken(
            userDto.id(), userDto.username(), userDto.role().name()
    );

    // Refresh Token Rotation - 새 리프레시 토큰 발급
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, userDto.username());

    // Registry 업데이트
    jwtRegistry.rotateJwtInformation(
            refreshToken,
            new JwtInformation(userDto, newAccessToken, newRefreshToken)
    );

    Cookie refreshCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setPath("/");
    response.addCookie(refreshCookie);

    return ResponseEntity.ok(new JwtDto(userDto, newAccessToken));
  }
}
