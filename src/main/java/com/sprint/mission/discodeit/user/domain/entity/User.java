package com.sprint.mission.discodeit.user.domain.entity;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.common.jpa.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
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

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private UserRole role;

  @Builder
  public User(String username,
      String email,
      BinaryContent profile,
      UserRole role) {
    Assert.hasText(username, "username is necessary");
    Assert.hasText(email, "email is necessary");
    Assert.notNull(role, "role is necessary");

    this.username = username;
    this.email = email;
    this.profile = profile;
    this.role = role;
  }

  public boolean updateUsername(String username) {
    return update(this.username, username, value -> this.username = value);
  }

  public boolean updateEmail(String email) {
    return update(this.email, email, value -> this.email = value);
  }

  public boolean updateProfile(BinaryContent profile) {
    return update(this.profile, profile, value -> this.profile = value);
  }

  public boolean updateRole(UserRole role) {
    return update(this.role, role, value -> this.role = value);
  }
}
