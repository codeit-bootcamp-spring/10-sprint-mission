package com.sprint.mission.discodeit.auth.application.service;

import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import com.sprint.mission.discodeit.auth.domain.exception.AuthException;
import com.sprint.mission.discodeit.auth.domain.exception.JWTErrorCode;
import com.sprint.mission.discodeit.auth.domain.provider.AuthUserResolver;
import com.sprint.mission.discodeit.auth.domain.provider.JwtRegistry;
import com.sprint.mission.discodeit.auth.presentation.dto.JwtLoginResponse;
import com.sprint.mission.discodeit.auth.presentation.mapper.AuthMapper;
import com.sprint.mission.discodeit.global.security.jwt.JwtProperties;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenService {

  private final JwtProperties jwtProperties;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final AuthMapper mapper;
  private final AuthUserResolver userResolver;

  public JwtLoginResponse reissue(String refreshToken) {
    jwtTokenProvider.validate(refreshToken);

    RefreshToken token = jwtRegistry.findByToken(refreshToken);
    if (token.isCompromised(refreshToken)) {
      jwtRegistry.invalidateAllByUserId(token.getUser().getId());
      throw new AuthException(JWTErrorCode.INVALID_REFRESH_TOKEN);
    }
    if (!token.isCurrentToken(refreshToken)) {
      throw new AuthException(JWTErrorCode.EXPIRED_TOKEN);
    }
    return issueTokensAndSave(token);
  }

  public JwtLoginResponse createJwtLogin(String stringUserId, String device) {
    UUID userId = UUID.fromString(stringUserId);
    User user = userResolver.getProxyOrThrow(userId);
    RefreshToken refreshEntity = RefreshToken.builder()
        .user(user)
        .token("")
        .device(device)
        .expiresAt(null)
        .build();
    return issueTokensAndSave(refreshEntity);
  }

  public void deleteRefreshToken(String token) {
    jwtRegistry.invalidateByToken(token);
  }

  private JwtLoginResponse issueTokensAndSave(RefreshToken token) {
    UUID userId = token.getUser().getId();
    User user = userResolver.getOrThrow(userId);
    UserRole role = user.getRole();
    String newAccessToken = jwtTokenProvider.generateAccessToken(userId, role);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
    long expirationSeconds = jwtProperties.refreshToken().expiration();
    Instant expiresAt = Instant.now().plusSeconds(expirationSeconds);
    token.rotate(newRefreshToken, expiresAt);
    jwtRegistry.register(token);
    return mapper.toDtoFrom(user, newAccessToken, newRefreshToken);
  }
}
