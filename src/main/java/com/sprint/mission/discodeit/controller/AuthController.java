package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;

  public AuthController(UserService userService, BinaryContentService binaryContentService,
      BinaryContentMapper binaryContentMapper, JwtTokenProvider jwtTokenProvider,
      DiscodeitUserDetailsService discodeitUserDetailsService) {
    this.userService = userService;
    this.binaryContentService = binaryContentService;
    this.binaryContentMapper = binaryContentMapper;
    this.jwtTokenProvider = jwtTokenProvider;
    this.discodeitUserDetailsService = discodeitUserDetailsService;
  }

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.status(203).build();
  }
// 삭제
//  @GetMapping("/me")
//  public ResponseEntity<UserDto> getMe(
//      @AuthenticationPrincipal DiscodeitUserDetails userDetails
//  ) {
//    UserDto userDto = userDetails.getUserDto();
//
//    return ResponseEntity.ok(userDto);
//  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(
      @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletResponse httpServletResponse
  ) {

    if (refreshToken == null || refreshToken.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    try {
      Map<String, Object> claims = jwtTokenProvider.getClaims(refreshToken);
      String username = (String) claims.get("sub");

      DiscodeitUserDetails userDetails =
          (DiscodeitUserDetails) discodeitUserDetailsService.loadUserByUsername(username);

      UserDto userDto = userDetails.getUserDto();

      Map<String, Object> accessClaims = Map.of(
          "roles", userDetails.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .toList()
      );

      String newAccessToken = jwtTokenProvider.generateAccessToken(
          accessClaims,
          userDto.email()
      );

      String newRefreshToken = jwtTokenProvider.generateRefreshToken(
          userDto.email()
      );

      Cookie cookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
      cookie.setHttpOnly(true);
      cookie.setPath("/");
      cookie.setMaxAge(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60);

      httpServletResponse.addCookie(cookie);

      return ResponseEntity.ok(new JwtDto(userDto, newAccessToken));

    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(
      @Valid @RequestBody UserRoleUpdateRequest request
  ) {
    UserResponse updated = userService.updateRole(request);

    BinaryContentDto profile = null;
    if (updated.profileImageId() != null) {
      BinaryContentResponse profileResponse =
          binaryContentService.find(updated.profileImageId());

      profile = binaryContentMapper.toDto(profileResponse);
    }

    UserDto userDto = new UserDto(
        updated.id(),
        updated.userName(),
        updated.email(),
        profile,
        updated.online(),
        updated.role()
    );

    return ResponseEntity.ok(userDto);
  }
}