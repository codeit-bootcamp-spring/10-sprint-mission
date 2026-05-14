package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.authdto.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j

public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject("User with username {username} not found")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "비밀번호가 일치하지 않음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject("Wrong password")
            )
        )
    }

    )
    public UserDto userLogin(@Valid @RequestBody LoginRequestDTO req) {
        log.trace("[Auth] 컨트롤러에서 인증 요청 받음");
        return authService.login(req); // 일단 Response DTO만 보내는걸로
        // 추후 로그인 기능을 서비스에서 구현?
    }

}
