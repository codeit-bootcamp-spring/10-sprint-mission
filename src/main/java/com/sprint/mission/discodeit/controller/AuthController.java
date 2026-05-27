package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final BasicAuthService authService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF Token: {}", tokenValue);
        return ResponseEntity
                .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
                .header("X-CSRF-TOKEN", tokenValue)
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserDto(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity
                .ok(((DiscodeitUserDetails) userDetails).getUserDTO());
    }

    // 왜 Put? Patch X??
    @PutMapping("/role")
    public ResponseEntity<UserDTO> updateUserRole(@Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity
                .ok(authService.updateRole(request));
    }
}
