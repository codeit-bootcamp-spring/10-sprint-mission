package com.sprint.mission.discodeit.global.security.constant;

import com.sprint.mission.discodeit.global.web.mvc.constant.WebConstants;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {

  // Login Parameters
  public static final String USERNAME_PARAMETER = "username";
  public static final String PASSWORD_PARAMETER = "password";

  // API Endpoints
  public static final String LOGIN_URL = WebConstants.SERVLET_PREFIX + "/auth/login";
  public static final String LOGOUT_URL = WebConstants.SERVLET_PREFIX + "/auth/logout";
  public static final String REFRESH_URL = WebConstants.SERVLET_PREFIX + "/auth/refresh";
  public static final String CSRF_TOKEN_URL = WebConstants.SERVLET_PREFIX + "/auth/csrf-token";
  public static final String USERS_URL = WebConstants.SERVLET_PREFIX + "/users";

  // Request Matchers
  public static final String[] PUBLIC_GET_PATHS = {
      CSRF_TOKEN_URL,
      "/ws/**",
      "/actuator/**",
      "/swagger-ui.html",
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/api.json"
  };

  public static final String[] PUBLIC_POST_PATHS = {
      LOGIN_URL,
      LOGOUT_URL,
      REFRESH_URL,
      USERS_URL
  };

  // CORS Configuration
  public static final List<String> CORS_ALLOWED_ORIGINS = List.of("http://localhost",
      "http://localhost:5173");
  public static final List<String> CORS_ALLOWED_HEADERS = List.of("authorization", "content-type",
      "x-xsrf-token", "x-device-id");
  public static final String CORS_REGISTER_PATTERN = "/**";
}
