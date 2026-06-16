package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtCookieManager;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.exception.auth.AuthenticationRequiredException;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 및 권한 관련 요청을 처리하는 컨트롤러 클래스입니다.
 * CSRF 토큰 발급, 권한 변경, 토큰 재발급 기능을 담당합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final UserService userService;
  private final AuthService authService;
  private final JwtCookieManager jwtCookieManager;

  @Override
  public ResponseEntity<Map<String, String>> getCsrfToken(CsrfToken csrfToken) {
    log.debug("[Auth] CSRF 토큰 발급 완료: {}", csrfToken.getHeaderName());

    return ResponseEntity
        .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .body(Map.of(
            "headerName", csrfToken.getHeaderName(),
            "token", csrfToken.getToken()
        ));
  }

  @Override
  public ResponseEntity<UserDto.Response> updateRole(UserRoleUpdateRequest request) {
    log.info("[Auth] 사용자 권한 변경 요청: UserId={}, Role={}", request.userId(), request.newRole());
    return ResponseEntity.ok(userService.updateRole(request.userId(), request.newRole()));
  }

  @Override
  public ResponseEntity<JwtDto> refresh(String refreshToken, HttpServletResponse response) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw AuthenticationRequiredException.withDetails("리프레시 토큰 쿠키가 없습니다.");
    }

    JwtInformation info = authService.refreshToken(refreshToken);
    
    // 전용 매니저를 통해 쿠키 설정 (보안 정책 통일)
    jwtCookieManager.addRefreshTokenCookie(response, info.getRefreshToken());

    log.info("[Auth] 액세스 토큰 재발급 완료: UserId={}", info.getUserDto().id());
    return ResponseEntity.ok(new JwtDto(info.getUserDto(), info.getAccessToken()));
  }
}
