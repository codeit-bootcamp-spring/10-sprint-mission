package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 인증이 되었지만 권한이 부족한 사용자가 접근했을 때 실행
@Component
@Slf4j
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException
    {
        log.warn("[AUTH_ACCESS_DENIED] 권한이 부족한 요청: method={}, uri={}, exception={}",
                request.getMethod(), request.getRequestURI(), accessDeniedException.getClass().getSimpleName());

        int status = HttpStatus.FORBIDDEN.value();

        // 예외 응답 생성
        ErrorResponse errorResponse = ErrorResponse.accessDenied(accessDeniedException, status);

        // response
        // 응답 상태 코드 403
        response.setStatus(status);
        // 응답 body가 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 한글 깨짐 방지를 위해 UTF-8 인코딩 설정
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // ErrorResponse 객체 -> JSON으로 변환 후 응답 Body에 담음
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
