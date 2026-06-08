package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private UUID userId;

  @Column(nullable = false, length = 1000)
  private String token;

  protected RefreshToken() {
  }

  public RefreshToken(UUID userId, String token) {
    this.userId = userId;
    this.token = token;
  }

  public void updateToken(String token) {
    this.token = token;
  }
}
