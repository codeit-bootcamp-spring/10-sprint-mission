package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.jwt.dto.AccessTokenClaims;
import com.sprint.mission.discodeit.auth.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import com.sprint.mission.discodeit.util.UserSessionManager;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final UserSessionManager userSessionManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;

  @Override
  public UserDto updateRole(UserRoleUpdateRequest request) {
    log.debug("사용자 권한 변경: userId={}, newRole={}", request.userId(), request.newRole());
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException().addDetail("userId", request.userId()));

    if (user.getRole() == request.newRole()) {
      log.info("사용자 권한 변경 요청(동일한 권한, 세션 만료x): userId={}", request.userId());
      return userMapper.toDto(user, userSessionManager.isOnline(user));
    }
    user.updateRole(request.newRole());
    userSessionManager.setOffline(user);
    log.info("사용자 권한 변경 및 세션 만료 완료: userId={}, newRole={}", request.userId(), request.newRole());
    return userMapper.toDto(user, false);
  }

  @Override
  public Entry<JwtDto, String> republishToken(String refreshToken) {
    log.debug("토큰 재발급 요청");
    if (refreshToken == null
        || !jwtTokenProvider.validateToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      log.debug("잘못된 리프레시 토큰");
      throw new BadCredentialsException("유효하지 않거나 만료된 리프레시 토큰");
    }
    UUID userId = UUID.fromString(jwtTokenProvider.getSubjectFromToken(refreshToken));
    log.debug("리프레시 토큰에서 아이디 추출 userId={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException().addDetail("userId", userId));

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) discodeitUserDetailsService.loadUserByUsername(
        user.getUsername());
    // 리프레시 토큰
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(
        userDetails.getUserDto().id().toString());
    log.debug("새로운 리프레시 토큰 발급 완료 userId={}", userId);

    // 액세스 토큰
    AccessTokenClaims accessTokenClaims = new AccessTokenClaims(userDetails.getUserDto().role());
    String accessToken = jwtTokenProvider.generateAccessToken(
        userDetails.getUserDto().id().toString(), accessTokenClaims);
    log.debug("새로운 액세스 토큰 발급 완료 userId={}", userId);
    JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), accessToken);

    JwtInformation jwtInformation = new JwtInformation(userDetails.getUserDto(), accessToken,
        newRefreshToken);
    jwtRegistry.rotateJwtInformation(refreshToken, jwtInformation);
    log.info("토큰 재발급 및 레지스트리 회전 완료: userId={}", userId);
    return new SimpleEntry<>(jwtDto, newRefreshToken);
  }

}
