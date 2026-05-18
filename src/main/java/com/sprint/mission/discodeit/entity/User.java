package com.sprint.mission.discodeit.entity;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@ToString(callSuper = true, exclude = {"profile", "userStatus"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false, length = 60)
  private String password;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private UserStatus userStatus;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role = Role.USER; // 회원가입 시 기본적으로 USER 권한 부여

  public User(String username, String email, String password, BinaryContent profile) {
    super();
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public void assignUserStatus(UserStatus status) { // 연관관계 편의 메서드
    this.userStatus = status;
    status.assignToUser(this);
  }

  public void updateName(String name) {
    this.username = name;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updateProfileImage(BinaryContent profile) {
    this.profile = profile;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public void updateRole(Role role) {
    this.role = role;
  }
}