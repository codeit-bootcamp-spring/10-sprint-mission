package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.dto.JwtDto;
import com.sprint.mission.discodeit.auth.dto.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

@RestController
@Slf4j
@Tag(name = "Auth")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    log.debug("내 정보 새로고침 요청: userid={}", userDetails.getUserDto().id());
    UserDto userDto = userDetails.getUserDto();
    log.info("내 정보 새로고침 성공: userId={}", userDto.id());
    return ResponseEntity.status(HttpStatus.OK)
        .body(userDto);
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> changeRole(@RequestBody UserRoleUpdateRequest updateRequest) {
    log.debug("user 권한 변경 요청: userId={}, newRole={}",
        updateRequest.newRole(), updateRequest.newRole());
    UserDto userDto = authService.updateRole(updateRequest);
    log.info("user 권한 변경 성공: userId={}, newRole={}", userDto.id(), userDto.role());
    return ResponseEntity.status(HttpStatus.OK)
        .body(userDto);
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> getRefreshToken(
      @CookieValue(name = "REFRESH_TOKEN") String refreshToken,
      HttpServletResponse response) {
    JwtInformation jwtInfo = authService.updateRefreshToken(refreshToken);

    // refresh 쿠키
    ResponseCookie cookie = jwtTokenProvider.generateRefreshTokenCookie(jwtInfo.getRefreshToken());
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    // JwtDto
    JwtDto jwtDto = new JwtDto(jwtInfo.getUserDto(), jwtInfo.getAccessToken());

    return ResponseEntity.status(HttpStatus.OK)
        .body(jwtDto);
  }
}
