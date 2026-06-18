package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.user.UserUpdateEvent;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

///  Spring Security 로그인 성공 후 실행되는 핸들러.
/// 로그인 인증 성공후, 서버가 Access Token과 Refresh Token을 발급하고,
/// 클라이언트가 이후 인증 상태를 유지할 수 있도록 전달하는 부분.
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;

  /**
   (1) 로그인 요청발생
   POST /api/auth/login
   {
    "username": "kwak"
    "password": "1234"
   }

   authentication = {
     principal = DiscodeitUserDetails {
       userDto = {
         id = "123e4567",
         username = "kwak",
         email = "kwak@test.com"
         },
         password = "$2a..."
     },
     authorities = ["ROLE_USER"],
     authenticated = true
   }
   **/
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    /// 로그인 사용자 객체 확인
    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      try {
        /// access Token 생성
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        /// refresh Token 생성
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        /// Refresh Token 쿠키 생성
        /**
         [ex]
         Set-Cookie:
           refreshToken=refresh.aaa.bbb;
           HttpOnly;
           MaxAge=604800;
           Path=/

         **/
        Cookie refreshCookie = tokenProvider.genereateRefreshTokenCookie(refreshToken);

        /// 쿠키 응답에 추가
        /// ex) refreshToken=refresh.aaa.bbb
        response.addCookie(refreshCookie);

        /// 클라이언트 응답 DTO 생성
        JwtDto jwtDto = new JwtDto(
            userDetails.getUserDto(),
            accessToken
        );

        /// HTTP 200 설정
        response.setStatus(HttpServletResponse.SC_OK);
        /// JSON으로 응답 내려주기.
        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

        /// JWT 정보 서버 저장.
        /// 로그아웃 처리, 토큰 강제 만료, 중복 로그인 제어.
        jwtRegistry.registerJwtInformation(
            new JwtInformation(
                userDetails.getUserDto(),
                accessToken,
                refreshToken
            )
        );
        User user = userRepository.findByIdWithProfile(userDetails.getUserDto().id())
                .orElseThrow(() -> UserNotFoundException.withId(userDetails.getUserDto().id()));

        UserDto userDto = userMapper.toDto(user);
        eventPublisher.publishEvent(new UserUpdateEvent(userDto));

        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
          cache.evict("all");
        }

        log.info("JWT access and refresh tokens issued for user: {}", userDetails.getUsername());

      } catch (JOSEException e) {
        log.error("Failed to generate JWT token for user: {}", userDetails.getUsername(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = new ErrorResponse(
            new RuntimeException("Token generation failed"),
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
      }
    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      ErrorResponse errorResponse = new ErrorResponse(
          new RuntimeException("Authentication failed: Invalid user details"),
          HttpServletResponse.SC_UNAUTHORIZED
      );
      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
  }

}