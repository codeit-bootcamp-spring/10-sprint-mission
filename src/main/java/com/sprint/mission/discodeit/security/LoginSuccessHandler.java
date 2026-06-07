//package com.sprint.mission.discodeit.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sprint.mission.discodeit.dto.data.UserDto;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class LoginSuccessHandler implements AuthenticationSuccessHandler {
//
//  private final ObjectMapper objectMapper;
//
//  @Override
//  public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
//      Authentication auth) throws IOException, ServletException {
//    UserDto userDto = ((DiscodeitUserDetails) auth.getPrincipal()).getUserDto();
//    res.setStatus(HttpServletResponse.SC_OK);
//    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
//    res.setCharacterEncoding("UTF-8");
//    objectMapper.writeValue(res.getWriter(), userDto);
//  }
//}
