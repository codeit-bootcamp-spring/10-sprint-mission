package com.sprint.mission.discodeit.security.jwt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.user.UserUpdateEvent;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class JwtLoginSuccessHandlerTest {

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private Authentication authentication;

  @Mock
  private JwtRegistry jwtRegistry;

  @Mock
  private CacheManager cacheManager;

  @Mock
  private UserMapper userMapper;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  private JwtLoginSuccessHandler jwtLoginSuccessHandler;
  private ObjectMapper objectMapper;
  private DiscodeitUserDetails userDetails;
  private UserDto userDto;
  private UUID userId;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    jwtLoginSuccessHandler = new JwtLoginSuccessHandler(
        objectMapper,
        tokenProvider,
        jwtRegistry,
        cacheManager,
        userMapper,
        userRepository,
        eventPublisher
    );

    userId = UUID.randomUUID();
    userDto = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        false,
        Role.USER
    );

    userDetails = new DiscodeitUserDetails(userDto, "encoded-password");
  }

  @Test
  @DisplayName("JWT 로그인 성공 핸들러 - 성공 테스트")
  void onAuthenticationSuccess_Success() throws Exception {
    // Given
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    when(response.getWriter()).thenReturn(printWriter);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    given(tokenProvider.generateAccessToken(any(DiscodeitUserDetails.class)))
        .willReturn("test.jwt.token");
    given(tokenProvider.generateRefreshToken(any(DiscodeitUserDetails.class)))
        .willReturn("test.refresh.token");
    given(tokenProvider.genereateRefreshTokenCookie("test.refresh.token"))
        .willReturn(new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, "test.refresh.token"));
    User user = new User("testuser", "test@example.com", "encoded-password", null);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    UserDto onlineUserDto = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        true,
        Role.USER
    );
    given(userMapper.toDto(user)).willReturn(onlineUserDto);

    // When
    jwtLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

    // Then
    verify(response).setCharacterEncoding("UTF-8");
    verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    verify(response).setStatus(HttpServletResponse.SC_OK);
    verify(tokenProvider).generateAccessToken(userDetails);
    verify(tokenProvider).generateRefreshToken(userDetails);
    verify(jwtRegistry).registerJwtInformation(any());
    verify(eventPublisher).publishEvent(any(UserUpdateEvent.class));

    String responseBody = stringWriter.toString();
    assert responseBody.contains("\"accessToken\":\"test.jwt.token\"");
    assert responseBody.contains("\"username\":\"testuser\"");
  }

  @Test
  @DisplayName("JWT 로그인 성공 핸들러 - 토큰 생성 실패 테스트")
  void onAuthenticationSuccess_TokenGenerationFailure() throws Exception {
    // Given
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    when(response.getWriter()).thenReturn(printWriter);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    given(tokenProvider.generateAccessToken(any(DiscodeitUserDetails.class)))
        .willThrow(new JOSEException("Token generation failed"));

    // When
    jwtLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

    // Then
    verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  @DisplayName("JWT 로그인 성공 핸들러 - 잘못된 사용자 정보 테스트")
  void onAuthenticationSuccess_InvalidUserDetails() throws Exception {
    // Given
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    when(response.getWriter()).thenReturn(printWriter);
    when(authentication.getPrincipal()).thenReturn("invalid-user-details");

    // When
    jwtLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

    // Then
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
