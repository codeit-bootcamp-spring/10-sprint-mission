package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * csrf 토큰 생성
     */
    @RequestMapping(value = "/csrf-token", method = RequestMethod.GET)
    @ApiResponse(responseCode = "203", description = "CSRF 토큰 요청이 성공적으로 수행됨")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String token = csrfToken.getToken();

        log.debug("[CSRF_TOKEN_REQUEST] CSRF 토큰 요청: token={}", token);

        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    /**
     * 세션을 활용해 현재 인증된 사용자 조회
     */
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    @Operation(summary = "세션을 활용한 현재 User 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "현재 인증된 User 정보 조회"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 User", content = @Content(examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "User with id {id} not found")))
    })
    public ResponseEntity<UserDto> getMe(
            @AuthenticationPrincipal DiscodeitUserDetails principal
    ) {
        UUID userId = principal.getUserDto().id();

        log.debug("[AUTH_ME] 현재 인증된 사용자 조회: userId={}", userId);

        UserDto userDto = userService.find(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    /**
     * 사용자 권한 수정
     */
    @RequestMapping(value = "/role", method = RequestMethod.PUT)
    @Operation(summary = "User 권한 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User 권한이 성공적으로 업데이트됨"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 User", content = @Content(examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "User with id {id} not found")))
    })
    public ResponseEntity<UserDto> updateUserRole(
            @RequestBody @Valid UserRoleUpdateRequest request
    ) {
        UserDto userDto = authService.updateUserRole(request);

        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }
}
