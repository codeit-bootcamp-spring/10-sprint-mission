package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.exception.user.InvalidEmailException;
import com.sprint.mission.discodeit.exception.user.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.InvalidUserNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  @Column(name = "username", nullable = false, unique = true, length = 50)
  private String username;

  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @Column(nullable = false, length = 60)
  private String password;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id", unique = true)
  private BinaryContent profile;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role = Role.USER;

  public User(String username, String email, String password) {
    super();
    validateUser(username, email, password);
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public void update(String newUsername, String newEmail, String newPassword,
      BinaryContent newProfileId) {

    if (newUsername != null && !newUsername.equals(this.username)) {
      validateUsername(newUsername);
      this.username = newUsername;
    }

    if (newEmail != null && !newEmail.equals(this.email)) {
      validateEmail(newEmail);
      this.email = newEmail;
    }

    if (newPassword != null && !newPassword.equals(this.password)) {
      validatePassword(newPassword);
      this.password = newPassword;
    }
    this.profile = newProfileId;
  }

  public void updateEncodedPassword(String encodedPassword) {
    this.password = encodedPassword;
  }

  public void updateRole(Role role) {
    this.role = role;
  }

  // 유저 생성 및 수정 시 준수해야 할 비즈니스 정책 (Fail-Fast)
  private void validateUser(String username, String email, String password) {
    validateUsername(username);
    validateEmail(email);
    validatePassword(password);
  }

  private void validateUsername(String username) {
    if (username == null || username.isBlank() || username.contains(" ") ||
        username.length() < 2 || username.length() > 50) {
      throw new InvalidUserNameException(username);
    }
  }

  private void validateEmail(String email) {
    if (email == null || email.isBlank() || email.contains(" ") || email.length() > 100) {
      throw new InvalidEmailException(email);
    }
  }

  private void validatePassword(String password) {
    if (password == null || password.length() < 8 || password.contains(" ")) {
      throw new InvalidPasswordException();
    }
  }

  @Override
  public String toString() {
    return String.format("User[닉네임: %s, 이메일: %s, User ID: %s]", username, email, getId());
  }
}
