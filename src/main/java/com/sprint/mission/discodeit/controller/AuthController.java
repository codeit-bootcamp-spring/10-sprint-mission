package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.authdto.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.jwtdto.JwtDto;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.JwtInformation;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Controller
public class AuthController {

  private final BasicAuthService basicAuthService;

  @PutMapping("/role")
  public ResponseEntity<UserDto> roleUpdate(
      @RequestBody RoleUpdateRequest req
  ) {
    return ResponseEntity.ok(basicAuthService.updateRole(req));
  }

  // refresh 토큰 엔드포인트
  // REFRESH_TOKEN 쿠키 값을 읽어들인다 (필수 아님)
  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(
      @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletResponse response
  ) {
    try {
      // 토큰 응답(유저 아이디, 액세스 토큰, 리프레시 토큰)
      JwtInformation jwtInformation = basicAuthService.refresh(refreshToken);
      Cookie refreshCookie = new Cookie("REFRESH_TOKEN", jwtInformation.getRefreshToken());
      refreshCookie.setHttpOnly(true);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(14 * 24 * 60 * 60);
      response.addCookie(refreshCookie);

      return ResponseEntity.ok(
          new JwtDto(jwtInformation.getUserDto(), jwtInformation.getAccessToken()));
    } catch (IllegalArgumentException e) {
      return invalidRefreshTokenResponse();
    }
  }

  private ResponseEntity<ErrorResponse> invalidRefreshTokenResponse() {
    ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
    ErrorResponse body = ErrorResponse.of(
        new IllegalArgumentException("Invalid refresh token"),
        errorCode,
        Map.of("token", "REFRESH_TOKEN")
    );
    return ResponseEntity.status(errorCode.getStatus()).body(body);
  }
}
