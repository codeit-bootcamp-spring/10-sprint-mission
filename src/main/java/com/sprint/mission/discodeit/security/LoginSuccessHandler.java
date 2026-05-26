package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.details.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        DiscodeitUserDetails userDetails =
                (DiscodeitUserDetails) authentication.getPrincipal();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        UserDto dto = userDetails.getUserDto();
        // 로그인 성공 응답은 이미 인증이 완료된 상태이므로 online=true로 내려주기 위함
        UserDto loginResponse = new UserDto(
                dto.id(),
                dto.username(),
                dto.email(),
                dto.profile(),
                true,
                dto.role()
        );

        objectMapper.writeValue(response.getWriter(), loginResponse);
    }
}
