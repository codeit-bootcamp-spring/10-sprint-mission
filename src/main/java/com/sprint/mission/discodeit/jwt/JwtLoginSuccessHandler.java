package com.sprint.mission.discodeit.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.CacheNames;
import com.sprint.mission.discodeit.config.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final CacheManager cacheManager;

    @Value("${discodeit.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        UserDto principalUser = userDetails.getUserDto();

        UUID userId = principalUser.id();
        String username = principalUser.username();
        String role = principalUser.role().name();

        String accessToken = jwtTokenProvider.generateAccessToken(userId, username, role);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, username, role);

        JwtInformation jwtInformation = new JwtInformation(principalUser, accessToken, refreshToken);
        jwtRegistry.registerJwtInformation(jwtInformation);

        Cookie refreshTokenCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) refreshTokenValiditySeconds);
        response.addCookie(refreshTokenCookie);

        JwtDto jwtDto = new JwtDto(accessToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

        Cache usersCache = cacheManager.getCache(CacheNames.USERS);
        if (usersCache != null) {
            usersCache.clear();
        }

        log.info("로그인 성공 (JWT): userId={}, username={}", userId, username);
    }
}
