package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.config.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.authdto.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.jwtdto.JwtDto;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Controller
public class AuthController {

  private final BasicAuthService basicAuthService;
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  
  @PutMapping("/role")
  public ResponseEntity<UserDto> roleUpdate(
      @RequestBody RoleUpdateRequest req
  ) {
    return ResponseEntity.ok(basicAuthService.updateRole(req));
  }

  // Refresh 토큰을 받아서 AccessToken을 재발급하는 엔드포인트
  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(
      @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken
  ) {
    // refresh 토큰이 비어있거나 문제가 있는 토큰이면 예외 처리
    if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validateRefreshToken(
        refreshToken)) {
      return invalidRefreshTokenResponse();
    }

    // 액세스 토큰을 요청 쿠키 값으로부터 읽어들이고 Access 토큰 재발급 및
    // authentication 객체를 가져옴
    // 가져온 authentication 객체를 커스텀 DiscodeitUserDetails로 변환
    try {
      String accessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
      Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
      DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

      return ResponseEntity.ok(new JwtDto(userDetails.getUserDto(), accessToken));
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
