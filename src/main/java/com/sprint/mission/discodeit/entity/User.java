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
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@ToString(callSuper = true, exclude = {"profile", "status"})
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
}