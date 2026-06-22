package com.sprint.mission.discodeit.auth.domain.entity;

import com.sprint.mission.discodeit.common.jpa.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "jwt_refresh_tokens")
public class RefreshToken extends BaseUpdatableEntity {

  @Column(name = "device", nullable = false)
  private String device;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "token", nullable = false, length = 512)
  private String token;

  @Column(name = "previous_token", length = 512)
  private String previousToken;

  @Builder
  public RefreshToken(User user, String device, String token, Instant expiresAt) {
    this.device = device;
    this.user = user;
    this.token = token;
    this.expiresAt = expiresAt;
  }

  public void rotate(String newToken, Instant expiresAt) {
    if (this.token.equals(newToken)) {
      throw new IllegalArgumentException("existing token is same new token");
    }
    this.previousToken = this.token;
    this.token = newToken;
    this.expiresAt = expiresAt;
  }

  public boolean isExpired(Instant current) {
    return expiresAt.isBefore(current);
  }

  public boolean hasToken(String token) {
    if (token == null) {
      return false;
    }
    return this.token.equals(token) || Objects.equals(this.previousToken, token);
  }

  public boolean isCurrentToken(String token) {
    return this.token.equals(token);
  }

  public boolean isCompromised(String token) {
    return Objects.equals(previousToken, token);
  }
}
