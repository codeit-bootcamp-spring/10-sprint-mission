package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        if (csrfToken != null) {
            log.debug("CSRF 토큰 요청: {}", csrfToken.getToken());
        }
        return ResponseEntity.status(203).build();
    }

    @PostMapping("refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null
                || !jwtTokenProvider.validateToken(refreshToken)
                || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new DiscodeitException(ErrorCode.INVALID_USER_CREDENTIALS);
        }

        Map<String, Object> claims = jwtTokenProvider.getClaims(refreshToken);
        String subject = (String) claims.get("sub");

        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService
                .loadUserByUsername(subject);

        Map<String, Object> newClaims = Map.of("roles", userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "," + b));

        String newAccessToken = jwtTokenProvider.generateAccessToken(newClaims, subject);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(newClaims, subject);

        jwtRegistry.rotateJwtInformation(refreshToken,
                new JwtInformation(userDetails.getUserDto(), newAccessToken, newRefreshToken));

        Cookie refreshCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, newRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(refreshTokenExpirationMinutes * 60);
        response.addCookie(refreshCookie);

        JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), newAccessToken);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), jwtDto);

        log.debug("토큰 재발급 성공 - subject: {}", subject);
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody @Valid UserRoleUpdateRequest request) {
        UserDto updatedUser = userService.updateRole(request);
        jwtRegistry.invalidateJwtInformationByUserId(request.userId());
        log.debug("권한 변경으로 인한 강제 로그아웃 - userId: {}", request.userId());
        return ResponseEntity.ok(updatedUser);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
