package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.details.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        DiscodeitUserDetails userDetails
                = (DiscodeitUserDetails) authentication.getPrincipal();

        UserDto dto = userDetails.getUserDto();

        UserDto loginResponse = new UserDto(
                dto.id(),
                dto.username(),
                dto.email(),
                dto.profile(),
                true,
                dto.role()
        );

        Map<String, Object> claims = Map.of("roles", dto.role());

        String accessToken = jwtTokenProvider.generateAccessToken(claims, dto.username());
        String refreshToken = jwtTokenProvider.generateRefreshToken(dto.username());
        Cookie refreshTokenCookie = new Cookie(
                "REFRESH_TOKEN",
                refreshToken
        );

        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(
                60 * 60 * 24 * 14
        );

        JwtInformation jwtInformation = new JwtInformation(
                loginResponse,
                accessToken,
                refreshToken
        );

        jwtRegistry.registerJwtInformation(jwtInformation);

        response.addCookie(refreshTokenCookie);

        JwtDto jwtDto = new JwtDto(loginResponse, accessToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), jwtDto);
    }
}
