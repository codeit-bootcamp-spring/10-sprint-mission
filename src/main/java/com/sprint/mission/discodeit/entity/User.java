package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;

import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Size(max = 50)
  @Column(length = 50, nullable = false)
  private String username;

  @Size(max = 100)
  @Column(length = 100, nullable = false)
  private String email;

  @Size(max = 60)
  @Column(length = 60, nullable = false)
  private String password;

  @Column(length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
      CascadeType.REMOVE}, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

//  @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST,
//      CascadeType.REMOVE}, orphanRemoval = true)
//  private UserStatus userStatus;

  public static User create(String username, String email, String password, BinaryContent profile) {
    return new User(username, email, password, profile);
  }

  private User(String username, String email, String password, BinaryContent profile) {
    if (username == null || email == null || password == null) {
      throw new IllegalArgumentException("사용자 이름, 이메일 또는 비밀번호가 NULL임");
    }
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

//  void setUserStatus(UserStatus userStatus) {
//    this.userStatus = userStatus;
//    if (userStatus.getUser() == null || userStatus.getUser() != this) {
//      this.userStatus.setUser(this);
//    }
//  }

  public void update(String newUsername, String newEmail, String newPassword,
      BinaryContent newProfile) {
    boolean anyValueUpdated = false;
    if (newUsername != null && !newUsername.isEmpty() && !newUsername.equals(this.username)) {
      this.username = newUsername;
    }
    if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(this.email)) {
      this.email = newEmail;
    }
    if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals(this.password)) {
      this.password = newPassword;
    }
    if (newProfile != null && (this.profile == null || !newProfile.getId()
        .equals(this.profile.getId()))) {
      this.profile = newProfile;
    }

  }

  public void updateRole(Role role) {
    this.role = role;
  }
}
