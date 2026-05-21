package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.config.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("me")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDto principalUser = userDetails.getUserDto();
        UserDto userDto = new UserDto(
            principalUser.id(),
            principalUser.username(),
            principalUser.email(),
            principalUser.profile(),
            principalUser.role(),
            true
        );
        log.debug("현재 사용자 조회: userId={}, username={}", userDto.id(), userDto.username());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody @Valid UserRoleUpdateRequest request) {
        log.info("사용자 역할 변경 요청: userId={}, newRole={}", request.userId(), request.role());
        UserDto userDto = authService.updateRole(request);
        log.debug("사용자 역할 변경 응답: {}", userDto);
        return ResponseEntity.ok(userDto);
    }
}
