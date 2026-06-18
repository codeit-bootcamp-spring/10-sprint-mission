package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public RefreshToken create(UUID userId, String token) {

    Instant expiredAt = Instant.now()
        .plus(
            jwtTokenProvider.getRefreshTokenExpirationMinutes(),
            ChronoUnit.MINUTES
        );

    return refreshTokenRepository.findByUserId(userId)
        .map(existingToken -> {
          existingToken.rotate(token, expiredAt);
          return refreshTokenRepository.save(existingToken);
        })
        .orElseGet(() -> {
          RefreshToken refreshToken = RefreshToken.builder()
              .token(token)
              .userId(userId)
              .expiredAt(expiredAt)
              .build();

          return refreshTokenRepository.save(refreshToken);
        });
  }

  @Transactional(readOnly = true)
  public RefreshToken findByToken(String token) {

    return refreshTokenRepository.findByToken(token)
        .orElseThrow(() ->
            new DiscodeitException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
        );
  }

  public void validate(RefreshToken refreshToken) {

    if (refreshToken.getExpiredAt().isBefore(Instant.now())) {
      throw new DiscodeitException(ErrorCode.REFRESH_TOKEN_EXPIRED);
    }
  }

  public RefreshToken rotate(UUID userId, String newToken) {

    RefreshToken refreshToken =
        refreshTokenRepository.findByUserId(userId)
            .orElseThrow(() ->
                new DiscodeitException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
            );

    Instant newExpiredAt = Instant.now()
        .plus(
            jwtTokenProvider.getRefreshTokenExpirationMinutes(),
            ChronoUnit.MINUTES
        );

    refreshToken.rotate(newToken, newExpiredAt);

    return refreshTokenRepository.save(refreshToken);
  }

  public void deleteByToken(String token) {
    refreshTokenRepository.deleteByToken(token);
  }
}
