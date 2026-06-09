package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;


  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
  }

//  @GetMapping("/me")
//  public ResponseEntity<UserDto> getCurrentUser(
//      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
//    //return ResponseEntity.ok(userDetails.getUserDto());는 기존 세션의 값을 가져오기때문에 직접 조회
//    UserDto dto = userService.find(userDetails.getUserDto().id());
//    return ResponseEntity.ok(dto);
//  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(
      @Valid @RequestBody UserRoleUpdateRequest userRoleUpdateRequest) {
    UserDto userDto = authService.updateRole(userRoleUpdateRequest);
    return ResponseEntity.ok(userDto);
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> republishToken(
      @CookieValue(value = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
      HttpServletResponse response) {
    Entry<JwtDto, String> republished = authService.republishToken(refreshToken);
    response.addCookie(jwtTokenProvider.getRefreshTokenCookie(republished.getValue()));
    return ResponseEntity.ok(republished.getKey());
  }

}
