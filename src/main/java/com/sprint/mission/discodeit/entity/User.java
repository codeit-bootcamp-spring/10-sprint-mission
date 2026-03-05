package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name="users")
@NoArgsConstructor
public class User extends BaseUpdatableEntity {
//  private static final long serialVersionUID = 1L;
//
//  private UUID id;
//  private Instant createdAt;
//  private Instant updatedAt;
  //
  @Column(nullable = false, length = 50)
  private String username;
  @Column(nullable = false, length = 100)
  private String email;
  @Column(nullable = false, length = 60)
  private String password;

  // BinaryContent 단방향 참조
  //0..1 (on delete set null)
  @OneToOne(optional = true)
  @JoinColumn(name="profile_id") // FK 지정 ->
  private BinaryContent profile;     // BinaryContent 참조.


  //user_statuses.user_id -> user.id
  //User가 status를 갖.
  @OneToOne(mappedBy = "user",
          optional = false, cascade = CascadeType.ALL,
          orphanRemoval = true)
  private UserStatus status;

  public User(String username, String email, String password, BinaryContent profile) {
//    this.id = UUID.randomUUID();
//    this.createdAt = Instant.now();

    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
    }
    if (newProfile != null && !newProfile.equals(this.profile)) {
      this.profile = newProfile;
    }

  }
}
