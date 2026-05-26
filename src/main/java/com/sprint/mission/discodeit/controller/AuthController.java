package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.authDto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.authDto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.authDto.jwt.TokenResultDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken){
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.status(203).build();
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(@Valid @RequestBody UserRoleUpdateRequest request){
        log.debug("유저 권한 수정 요청: {}", request.getUserId());
        UserDto userDto = authService.updateRole(request);

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> reissueAccessToken(
            @CookieValue(value = "REFRESH_TOKEN", required = false) String oldRefreshToken,
            HttpServletResponse response){
        if(oldRefreshToken == null){
            // 커스텀 처리하기
            throw new IllegalArgumentException("리프레시 토큰이 없습니다.");
        }

        TokenResultDto result = tokenService.reissueToken(oldRefreshToken);

        User user = userRepository.findByEmail(result.getEmail())
                .orElseThrow(() -> new UserNotFoundException(result.getEmail()));
        UserDto userDto = userMapper.toDto(user, true);

        Cookie newRefreshTokenCookie = new Cookie("REFRESH_TOKEN", result.getNewRefreshToken());
        newRefreshTokenCookie.setHttpOnly(true);
        newRefreshTokenCookie.setPath("/");
        newRefreshTokenCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(newRefreshTokenCookie);

        JwtDto responseDto = new JwtDto(userDto, result.getNewAccessToken());

        return ResponseEntity.ok(responseDto);
    }

}
