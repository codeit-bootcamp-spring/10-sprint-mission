package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.auth.controller.dto.JwtLoginResponse;
import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import com.sprint.mission.discodeit.auth.domain.exception.AuthErrorCode;
import com.sprint.mission.discodeit.auth.domain.exception.AuthException;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.security.jwt.JwtProperties;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.user.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.exception.UserException;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

  private final JwtProperties jwtProperties;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  public UserResponse updateRole(RoleUpdateRequest request) {
    User user = findById(request.getUserId());
    userMapper.partialUpdate(request, user);
    jwtRegistry.invalidateAllByUserId(request.getUserId());
    return userMapper.toDto(user);
  }

  public JwtLoginResponse reissue(String refreshToken) {
    jwtTokenProvider.validate(refreshToken);

    RefreshToken token = jwtRegistry.findByToken(refreshToken);

    if (Objects.equals(token.getPreviousToken(), refreshToken)) {
      jwtRegistry.invalidateAllByUserId(token.getUser().getId());
      throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }
    if (!token.getToken().equals(refreshToken)) {
      throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
    }
    return issueTokensAndSave(token.getUser(), token);
  }

  public JwtLoginResponse createJwtLogin(UUID userId, String device) {
    DomainServiceSupport.requireOrThrow(userId, userRepository::existsById,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));

    User user = userRepository.getReferenceById(userId);
    RefreshToken refreshEntity = new RefreshToken(user, device, "", Instant.now());
    return issueTokensAndSave(user, refreshEntity);
  }

  public void deleteRefreshToken(String token) {
    jwtRegistry.invalidateByToken(token);
  }

  private JwtLoginResponse issueTokensAndSave(User user, RefreshToken refreshEntity) {
    String newAccessToken = jwtTokenProvider.generateAccessToken(
        user.getId().toString(), user.getRole().name());
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

    long expirationSeconds = jwtProperties.refreshToken().expiration();
    Instant expiresAt = Instant.now().plusSeconds(expirationSeconds);

    refreshEntity.rotate(newRefreshToken, expiresAt);
    jwtRegistry.register(refreshEntity);

    return new JwtLoginResponse(userMapper.toDto(user), newAccessToken, newRefreshToken);
  }

  private User findById(UUID id) {
    return DomainServiceSupport.getOrThrow(id, userRepository::findById,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }
}
