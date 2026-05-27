package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();   // response에 set-cookie
        log.debug("CSRF 토큰 요청: {}", tokenValue);
        return ResponseEntity
                .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        return ResponseEntity.ok(userDetails.getUserDto());
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateRole(
            @RequestBody UserRoleUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateRole(request));
    }

//    private final AuthService authService;
//
//    // 로그인
//    @Operation(summary = "로그인", description = "사용자 인증 수행")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "로그인 성공",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = UserDto.class),
//                            examples = @ExampleObject(value = """
//                                    {
//                                      "userId": "550e8400-e29b-41d4-a716-446655440000",
//                                                          "createdAt": "2026-01-01T00:00:00Z",
//                                                          "updatedAt": "2026-02-20T14:00:00Z",
//                                                          "newUsername": "달선",
//                                                          "status": "ONLINE",
//                                                          "email": "dalsun@naver.com",
//                                                          "profileId": "3f3fb215-32aa-4281-904c-45b9dd8b96fb",
//                                                          "online": true
//                                       }
//                                    """))),
//            @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자입니다.")
//
//    })
//
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public ResponseEntity<UserDto> login(@RequestBody @Valid LoginRequest dto) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(authService.login(dto));
//    }
}
