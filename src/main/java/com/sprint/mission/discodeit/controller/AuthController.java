package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserInfo(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 현재 사용자의 online 상태를 반영하기 위함
        UserDto dto = userService.findByUserId(userDetails.getUserDto().id());
        log.debug("[USER INFO] 사용자 정보 요청: userId={}", dto.id());

        return ResponseEntity.ok(dto);
    }

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
        log.debug("[CSRF TOKEN] CSRF 토큰 요청: {}", tokenValue);

        // 응답 203으로 설정
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }
}
