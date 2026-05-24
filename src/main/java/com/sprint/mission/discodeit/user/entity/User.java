package com.sprint.mission.discodeit.user.entity;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.common.entity.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.entity.constant.UserRole;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Column(unique = true, nullable = false, length = 50)
  private String username;

  @Column(unique = true, nullable = false, length = 100)
  private String email;

  @OneToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
      orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(
      mappedBy = "user",
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
      orphanRemoval = true)
  private UserStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private UserRole role;

  @Builder
  public User(String username,
      String email,
      BinaryContent profile,
      UserStatus status,
      UserRole role) {
    this.username = username;
    this.email = email;
    this.profile = profile;
    this.status = status;
    this.role = role;
  }

  public boolean isOnline() {
    return status.isOnline(Instant.now());
  }

  public void setStatus(UserStatus status) {
    this.status = status;
    status.setUser(this);
  }
}
