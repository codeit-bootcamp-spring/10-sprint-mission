package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {
  ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);
  ResponseEntity<UserDto> updateUserRole(UserRoleUpdateRequest request);
  ResponseEntity<JwtDto> refresh(String refreshToken, HttpServletResponse response);
} 