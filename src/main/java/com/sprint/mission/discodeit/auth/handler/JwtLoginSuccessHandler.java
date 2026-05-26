package com.sprint.mission.discodeit.auth.handler;

import com.google.gson.Gson;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.details.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final Gson gson;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

        UserDto userDto = userDetails.getUserDto();

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDto.username());
        claims.put("roles", userDto.role());
        claims.put("memberId", userDto.id());

        String subject = userDto.email();

        String accessToken = jwtTokenProvider.generateAccessToken(claims, subject);
        String refreshToken = jwtTokenProvider.generateRefreshToken(subject);

        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);

        response.addCookie(refreshTokenCookie);

        JwtDto jwtDto = new JwtDto(userDto, accessToken);

        log.info("로그인 성공: {}", userDto.email());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                gson.toJson(jwtDto)
        );
    }
}
