package com.sprint.mission.discodeit.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.service.SseService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final CacheManager cacheManager;
    private final SseService sseService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        JwtInformation jwtInformation = new JwtInformation(
                userDetails.getUserDto().id(),
                userDetails.getUsername(),
                userDetails.getUserDto().role(),
                accessToken,
                refreshToken,
                jwtTokenProvider.getExpirationTime(accessToken).toInstant(),
                jwtTokenProvider.getExpirationTime(refreshToken).toInstant()
        );
        jwtRegistry.registerJwtInformation(jwtInformation);

        ResponseCookie refreshTokenCookie = ResponseCookie.from(
                JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
                refreshToken
            )
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Strict")
            .maxAge(Duration.ofMinutes(jwtTokenProvider.getRefreshTokenExpirationMinutes()))
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        JwtDto jwtDto = new JwtDto(
                userDetails.toAuthenticatedUserDto(),
                accessToken
        );

        Cache usersCache = cacheManager.getCache("users");
        if (usersCache != null) {
            usersCache.clear();
        }
        sseService.broadcast("users.updated", userDetails.toAuthenticatedUserDto());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), jwtDto);
    }
}
