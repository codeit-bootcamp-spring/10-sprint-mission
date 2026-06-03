package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.auth.AuthTokenResult;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF token requested: {}", tokenValue);

    return ResponseEntity.status(203).build(); // 203 Non-Authoritative Information 반환
  }

  @Override
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(@Valid @RequestBody UserRoleUpdateRequest request) {
    log.info("Role update requested - userId: {}, newRole: {}", request.userId(),
        request.newRole());

    User user = userService.updateUserRole(request.userId(), request.newRole());

    return ResponseEntity.ok(userMapper.toDto(user));
  }

  @Override
  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(
      @CookieValue(value = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
      HttpServletResponse response) {

    // 1. 쿠키(리프레시 토큰) 존재 여부 검증
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new InvalidTokenException(Map.of("reason", "리프레시 토큰 쿠키가 존재하지 않습니다."));
    }

    // 2. AuthService 호출하여 새 토큰 쌍 생성
    AuthTokenResult tokenResult = authService.refresh(refreshToken);

    // 3. 새로운 리프레시 토큰을 쿠키에 덮어쓰기
    Cookie newRefreshTokenCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
        tokenResult.refreshToken());
    newRefreshTokenCookie.setHttpOnly(true);
    newRefreshTokenCookie.setPath("/");
    response.addCookie(newRefreshTokenCookie);

    // 4. 새로운 엑세스 토큰과 유저 정보를 JwtDto에 담아 응답
    JwtDto jwtDto = new JwtDto(tokenResult.userDto(), tokenResult.accessToken());
    return ResponseEntity.ok(jwtDto);
  }
}
