package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile; // BinaryContent의 id

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;


  public User(String username, String email, String password, BinaryContent profile, Role role) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
    this.role = role;
  }

  public User() {

  }


  public void update(String newUsername, String newEmail, String newPassword,
      BinaryContent binaryContent) {
    boolean anyValueUpdated = false;
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
      anyValueUpdated = true;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
      anyValueUpdated = true;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
      anyValueUpdated = true;
    }
    if (binaryContent != null && (this.profile == null || !binaryContent.getId()
        .equals(this.profile.getId()))) {
      this.profile = binaryContent;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

  public void updateRole(Role role) {
    this.role = role;
  }
}
