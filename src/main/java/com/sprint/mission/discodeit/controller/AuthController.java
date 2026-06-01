package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    // 요구사항에 PUT으로 명시되어 있어서 PATCH가 아닌 PUT 사용
    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(
            @Valid @RequestBody UserRoleUpdateRequest request
            ) {
        UserDto response = userService.updateRole(request);

        log.debug("[USER ROLE UPDATE] 사용자 역할 수정 요청: userId={}", request.userId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("[CSRF TOKEN] CSRF 토큰 요청");

        // 응답 203으로 설정
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        JwtDto dto = authService.refresh(refreshToken, response);

        return ResponseEntity.ok(dto);
    }
}
