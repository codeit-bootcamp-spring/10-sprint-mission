package com.sprint.mission.discodeit.auth.presentation.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.auth.application.service.JwtTokenService;
import com.sprint.mission.discodeit.auth.infra.web.CookieProvider;
import com.sprint.mission.discodeit.auth.presentation.dto.JwtLoginResponse;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private JwtTokenService jwtTokenService;

  @MockitoBean
  private CookieProvider cookieProvider;

  @Test
  @DisplayName("csrfToken - CSRF 토큰을 요청하면 상태 코드 204를 반환한다.")
  @WithMockUser
  void csrfToken_success() throws Exception {
    mockMvc.perform(get("/auth/csrf-token")
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("refresh - Refresh Token이 쿠키에 없으면 401 Unauthorized를 반환한다.")
  void refresh_fail_unauthorized() throws Exception {
    mockMvc.perform(post("/auth/refresh")
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("refresh - 유효한 Refresh Token이 있으면 토큰을 재발급하고 쿠키를 설정하여 반환한다.")
  void refresh_success() throws Exception {
    String refreshToken = "valid.refresh.token";
    String newAccessToken = "new.access.token";
    String newRefreshToken = "new.refresh.token";
    JwtLoginResponse response = new JwtLoginResponse(null, newAccessToken, newRefreshToken);
    ResponseCookie responseCookie = ResponseCookie.from("REFRESH_TOKEN", newRefreshToken)
        .maxAge(Duration.ofDays(7))
        .build();

    given(jwtTokenService.reissue(refreshToken)).willReturn(response);
    given(cookieProvider.createRefreshTokenCookie(newRefreshToken)).willReturn(responseCookie);

    mockMvc.perform(post("/auth/refresh")
            .cookie(new Cookie("REFRESH_TOKEN", refreshToken))
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.SET_COOKIE, responseCookie.toString()))
        .andExpect(jsonPath("$.accessToken").value(newAccessToken));
  }
}
