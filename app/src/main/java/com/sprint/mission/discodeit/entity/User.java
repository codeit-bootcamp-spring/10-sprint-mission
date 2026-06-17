package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "USERS")
public class User extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PROFILE_ID")
  private BinaryContent profile;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;

  public User(String username, String email, String password, BinaryContent profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
    this.role = Role.USER;
  }

  public void update(String newUsername, String newEmail, String encodedPassword) {
    updateIfChanged(this.username, newUsername, val -> this.username = val);
    updateIfChanged(this.email, newEmail, val -> this.email = val);
    updateIfChanged(this.password, encodedPassword, val -> this.password = val);
  }

  public void updateRole(Role role) {
    updateIfChanged(this.role, role, val -> this.role = val);
  }

  public void updateProfile(BinaryContent newProfile) {
    this.profile = newProfile;
  }
}
