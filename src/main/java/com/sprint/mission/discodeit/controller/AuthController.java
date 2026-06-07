package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.exception.jwt.InvalidTokenException;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtProperties;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtProperties jwtProperties;
  private final UserService userService;
  private final JwtRegistry jwtRegistry;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity
        .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .build();
  }

  @PutMapping("role")
  public ResponseEntity<UserDto> updateRole(@Valid @RequestBody UserRoleUpdateRequest request) {
    log.info("사용자 권한 수정 요청: userId={}, newRole={}", request.userId(), request.newRole());
    UserDto updatedUser = userService.updateRole(request.userId(), request.newRole());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }

  @PostMapping("refresh")
  public ResponseEntity<JwtDto> refresh(
      // @CookieValue: 요청 쿠키에서 값을 자동으로 꺼내주는 어노테이션
      // required = false -> 쿠키가 없어도 예외 발생 안함, 대신 null로 들어옴
      @CookieValue(value = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
      HttpServletResponse response
  ) {
    // 쿠키 자체가 없는 경우 (로그아웃 후 재발급 시도, 브라우저 쿠키 삭제, 쿠키 만료)
    if (refreshToken == null) {
      throw new InvalidTokenException();
    }

    // registry에 없는 토큰 (로그아웃/강제 무효화) -> 401
    // 서버가 발급한 후 아직 유효한 상태인지 or 로그아웃/강제 무효화된 토큰이 아닌지 검증
    if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      throw new InvalidTokenException();
    }

    UserDto userDto;
    try {
      // 리프레시 토큰 파싱 + 유효성 검사 + 사용자 정보 추출
      userDto = jwtTokenProvider.extractUserDtoFromRefreshToken(refreshToken);
      // extractUserDtoFromRefreshToken() - 토큰 파싱
      // - 서명 검증 (위변조 여부) -> 만료 시각(exp) 확인
      // -> type 클레임이 'refresh'인지 확인 -> claims에서 userId, username, role 꺼내 UserDto 생성
    } catch (IllegalArgumentException e) {
      throw new InvalidTokenException();
    }

    // 새 토큰 발급
    String newAccessToken = jwtTokenProvider.generateAccessToken(userDto);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDto);

    // Registry Token Rotation 기존 토큰 -> 새 토큰으로 교체
    jwtRegistry.rotateJwtInformation(refreshToken, // 교체 대상인 기존 Refresh Token
        new JwtInformation(userDto, newAccessToken, newRefreshToken)); // 새 토큰 정보로 구성한 객체
    // 기존 JwtInformation 제거하고 새 JwtInformation 등록

    // 새 리프레시 토큰을 쿠키에 덮어씀 (기존 쿠키 교체)
    // 브라우저는 같은 이름의 쿠키를 받으면 자동으로 덮어쓴다.
    Cookie cookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, newRefreshToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (jwtProperties.refreshTokenExpiry() / 1000));
    response.addCookie(cookie);

    // 사용자 정보 + 새 Access Token을 담은 DTO
    return ResponseEntity.ok(new JwtDto(userDto, newAccessToken));
  }
}
