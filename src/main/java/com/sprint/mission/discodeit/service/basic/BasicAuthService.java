package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.exception.auth.AuthenticationRequiredException;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.event.UserEvents;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * 사용자 인증 및 토큰 관리를 담당하는 기본 서비스 클래스입니다.
 * 리프레시 토큰을 이용한 토큰 갱신 및 사용자 세션 만료 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * 리프레시 토큰을 검증하고 새로운 액세스 토큰과 리프레시 토큰을 발급합니다. (Token Rotation)
   *
   * @param refreshToken 현재 사용 중인 리프레시 토큰
   * @return 새로운 토큰 정보 (JwtInformation)
   * @throws AuthenticationRequiredException 토큰이 유효하지 않거나 만료된 경우
   */
  @Override
  public JwtInformation refreshToken(String refreshToken) {
    validateRefreshToken(refreshToken);

    String username = jwtTokenProvider.getUsername(refreshToken);
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

    String roles = extractRoles(userDetails);
    JwtInformation newInfo = createNewJwtInformation(userDetails, roles, username);

    // 기존 토큰을 무효화하고 새 토큰으로 교체 (Rotation 정책)
    jwtRegistry.rotateJwtInformation(refreshToken, newInfo);
    
    log.info("[Auth] 토큰 갱신 완료: Username={}", username);
    return newInfo;
  }

  /**
   * 특정 사용자의 모든 활성 세션(토큰)을 강제로 만료시킵니다.
   * 주로 권한 변경이나 계정 삭제 시 호출됩니다.
   *
   * @param userId 세션을 만료할 사용자 ID
   */
  @Override
  public void expireUserSessions(UUID userId) {
    jwtRegistry.invalidateJwtInformationByUserId(userId);
    eventPublisher.publishEvent(new UserEvents.OnlineStatusChanged(userId, false));
    log.info("[Auth] 사용자 세션 강제 만료: UserId={}", userId);
  }

  // --- Private Helpers ---

  private void validateRefreshToken(String refreshToken) {
    if (!jwtTokenProvider.validateToken(refreshToken) || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      log.warn("[Auth] 유효하지 않은 리프레시 토큰 요청");
      throw AuthenticationRequiredException.withDetails("유효하지 않거나 만료된 리프레시 토큰입니다.");
    }
  }

  private String extractRoles(DiscodeitUserDetails userDetails) {
    return userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));
  }

  private JwtInformation createNewJwtInformation(DiscodeitUserDetails userDetails, String roles, String username) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", roles);

    String newAccessToken = jwtTokenProvider.generateAccessToken(claims, username);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

    return JwtInformation.builder()
        .userDto(userDetails.getUserDto())
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }
}