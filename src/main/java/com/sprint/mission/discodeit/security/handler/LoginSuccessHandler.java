package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
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
import java.nio.charset.StandardCharsets;

// 로그인 인증 성공 후 실행되는 핸들러 클래스로, 사용자 인증이 성공했을 때 어떻게 처리할지를 정하는 객체
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, // HTTP 요청
            HttpServletResponse response, // HTTP 응답
            Authentication authentication // 인증 성공 결과 객체
    ) throws IOException, ServletException
    {
        // authentication = 인증 성공 결과 객체
        // .getPrincipal()에 userDetails가 들어가 있다.
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

        UserDto userDto = userDetails.getUserDto();

        UserDto refreshedUserDto = new UserDto(
                userDto.id(),
                userDto.username(),
                userDto.email(),
                userDto.profile(),
                true,
                userDto.role()
        );

        log.info("[AUTH_LOGIN_SUCCESS] 로그인 성공: userId={}", refreshedUserDto.id());

        // response
        // 응답 상태 코드 200
        response.setStatus(HttpStatus.OK.value());
        // 응답 body가 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 한글 깨짐 방지를 위해 UTF-8 인코딩 설정
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // UserDto 객체 -> JSON으로 변환 후 응답 Body에 담음
        objectMapper.writeValue(response.getWriter(), refreshedUserDto);
    }
    // 이후 SecurityContext에 Authentication이 저장됨
}
