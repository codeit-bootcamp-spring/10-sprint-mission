package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Column(length = 50, nullable = false, unique = true)
  private String username;

  @Column(length = 100, nullable = false, unique = true)
  private String email;

  @Column(length = 60, nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
      CascadeType.REMOVE}, orphanRemoval = true)
  @JoinColumn(name = "profile_id", unique = true)
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST,
      CascadeType.REMOVE}, orphanRemoval = true)
  private UserStatus userStatus;

  public User(String username, String email, String password, BinaryContent profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public void update(String newUsername, String newEmail, String newPassword,
      BinaryContent newProfile) {
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
    }
    if (newProfile != null && newProfile != this.profile) {
      this.profile = newProfile;
    }
  }

  public void setUserStatus(UserStatus userStatus) {
    if (this.userStatus == userStatus) {
      return;
    }

    if (this.userStatus != null) {
      this.userStatus.assignUser(null);
    }

    this.userStatus = userStatus;

    if (userStatus != null && userStatus.getUser() != this) {
      userStatus.assignUser(this);
    }
  }

  public void removeUserStatus() {
    setUserStatus(null);
  }
}