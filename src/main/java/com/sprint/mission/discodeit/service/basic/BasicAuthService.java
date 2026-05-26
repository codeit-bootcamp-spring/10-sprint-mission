package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthTokenResult;
import com.sprint.mission.discodeit.dto.auth.JwtInformation;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  @Transactional(readOnly = true)
  public AuthTokenResult refresh(String refreshToken) {

    // 1. 리프레시 토큰 유효성 검증
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      log.warn("Invalid or expired refresh token provided.");
      throw new InvalidTokenException(Map.of("token", "토큰이 만료되었거나 위조되었습니다."));
    }

    // 2. 토큰에서 유저 ID 추출
    String subject = jwtTokenProvider.getSubjectFromToken(refreshToken);
    UUID userId = UUID.fromString(subject);

    // 3. 유저 검증 및 UserDetails 로드 (DB 조회)
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) discodeitUserDetailsService.loadUserById(
        userId);

    // 4. Registry에 없는 리프레시 토큰이라면 예외 발생
    if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      throw new InvalidTokenException(Map.of("token", "폐기된 리프레시 토큰입니다. 다시 로그인하세요."));
    }

    // 5. 새로운 토큰 쌍 발급
    String newAccessToken = jwtTokenProvider.createAccessToken(userDetails);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(userDetails);

    // 6. 토큰 로테이션 (기존 토큰 삭제 및 새 토큰 등록)
    Instant expiresAt = jwtTokenProvider.getExpirationFromToken(newRefreshToken);
    JwtInformation newJwtInfo = new JwtInformation(userDetails.getUserDto(), newAccessToken,
        newRefreshToken, expiresAt);
    jwtRegistry.rotateJwtInformation(refreshToken, newJwtInfo);

    log.info("Token refreshed successfully for user ID: {}", userId);

    // 5. 결과 반환
    return new AuthTokenResult(newAccessToken, newRefreshToken, userDetails.getUserDto());
  }
}
