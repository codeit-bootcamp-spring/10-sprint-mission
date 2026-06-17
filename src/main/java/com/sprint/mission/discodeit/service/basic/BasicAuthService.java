package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.JwtRefreshResultDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.auth.JwtException;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final JwtRegistry jwtRegistry;

    @Override
    public JwtRefreshResultDto refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("missing");
        }

        Map<String, Object> claims;
        try {
            claims = jwtTokenProvider.getClaims(refreshToken);
        } catch (JwtException e) {
            log.debug("리프레시 토큰 검증 실패: {}", e.getMessage());
            throw new InvalidRefreshTokenException("invalid");
        }

        // 레지스트리에 등록된 토큰인지 검증 (재사용·강제 무효화 방어)
        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException("not-registered");
        }

        UUID userId;
        try {
            userId = UUID.fromString((String) claims.get("sub"));
        } catch (RuntimeException e) {
            throw new InvalidRefreshTokenException("malformed-subject");
        }

        UserDto userDto = userService.find(userId);

        String subject = userDto.id().toString();
        Map<String, Object> newClaims = Map.of(
                "username", userDto.username(),
                "role", userDto.role().name()
        );
        String newAccessToken = jwtTokenProvider.generateAccessToken(newClaims, subject);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(subject);

        // 토큰 로테이션 (기존 JwtInformation의 토큰만 교체 — 식별자 유지)
        jwtRegistry.rotateJwtInformation(
                refreshToken,
                new JwtInformation(userDto, newAccessToken, newRefreshToken)
        );

        return new JwtRefreshResultDto(userDto, newAccessToken, newRefreshToken);
    }
}
