package com.sprint.mission.discodeit.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** 로그인 성공후 처리 담당 Handler
////Spring Security의 form login 인증이 성공했을 때 마지막 응답을 어떻게 만들지 결정하는 컴포넌트
/// 기본 구현체: SavedRequestAwareAuthenticationSuccessHandler
 로그인 성공
 -> 원래 가려던 페이지가 있으면 그 페이지로 redirect
 -> 없으면 기본 URL로 redirect
 이런 흐름으로 맞춰져 있는데 discodeit 프로젝트는 REST API + SPA 구조라서 JSON 형태의 응답을 보내줘야함.
**/
//그래서 기본 핸들러가 아닌 LoginSuccessHandler 만들어서 로그인 성공시 UserDto JSON을 200으로 응답하라는 의미.
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        //현재 로그인한 사용자 객체
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        UserDto userDto = userDetails.getUserDto();

        //응답상태 200OK로 설정
        response.setStatus(HttpServletResponse.SC_OK);

        //응답타입 Content-Type: application/json로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        //한글 깨짐 방지
        response.setCharacterEncoding("UTF-8");

        //userDto객체를 JSON으로 변환해서 응답 body에 쓴다.
        objectMapper.writeValue(response.getWriter(), userDto);
    }
}
