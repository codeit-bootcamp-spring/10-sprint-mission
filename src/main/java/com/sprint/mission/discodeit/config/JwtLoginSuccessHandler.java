package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
            throw new IllegalStateException(
                    "Unexpected principal type: " + authentication.getPrincipal().getClass());
        }
        UserDto userDto = userDetails.getUserDto();
        String subject = userDto.id().toString();
        Map<String, Object> claims = Map.of(
                "username", userDto.username(),
                "role", userDto.role().name()
        );
        String accessToken = jwtTokenProvider.generateAccessToken(claims, subject);
        String refreshToken = jwtTokenProvider.generateRefreshToken(subject);

        // 동시 로그인 제한 정책에 따라 등록 (기존 세션은 registry 내부에서 자동 무효화)
        jwtRegistry.registerJwtInformation(new JwtInformation(userDto, accessToken, refreshToken));

        ResponseCookie refreshCookie = ResponseCookie.from(
                        JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60L)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        log.info("로그인 성공: userId={}, username={}", userDto.id(), userDto.username());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), new JwtDto(userDto, accessToken));
    }
}
