package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public JwtDto refresh(String refreshToken, HttpServletResponse response) {
        if (
                refreshToken == null
                        || !jwtTokenProvider.validationToken(refreshToken)
                        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)
        ) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtTokenProvider.getSubject(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 유저가 존재하지 않음: username={}", username);
                    return new UserNotFoundException(username);
                });
        // refresh 요청에 성공 = 현재 인증 갱신 중인 사용자라서 online=true
        UserDto userDto = userMapper.toDto(user, true);

        Map<String, Object> claims = Map.of("roles", user.getRole().name());

        String newAccessToken = jwtTokenProvider.generateAccessToken(claims, username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        JwtInformation newJwtInformation = new JwtInformation(
                userDto,
                newAccessToken,
                newRefreshToken
        );

        jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

        Cookie cookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);
        response.addCookie(cookie);

        return new JwtDto(userDto, newAccessToken);
    }
}
