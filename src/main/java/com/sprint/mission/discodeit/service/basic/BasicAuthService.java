package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthTokenResult;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
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

    // 4. 새로운 토큰 쌍 발급 (Rotation)
    String newAccessToken = jwtTokenProvider.createAccessToken(userDetails);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(userDetails);

    log.info("Token refreshed successfully for user ID: {}", userId);

    // 5. 결과 반환
    return new AuthTokenResult(newAccessToken, newRefreshToken, userDetails.getUserDto());
  }
}
