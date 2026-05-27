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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "users")
@Getter
public class User extends BaseUpdatableEntity {

  @Column(nullable = false, length = 50)
  private String username;

  @Column(nullable = false, length = 100)
  private String email;

  @Column(nullable = false, length = 60)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  public User(String username, String password, String email) {
    super();
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = Role.USER;
  }

  public void updateUserName(String username) {
    this.username = username;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updateProfile(BinaryContent profile) {
    this.profile = profile;
  }

  public void updateRole(Role role) {
    this.role = role;
  }
}
