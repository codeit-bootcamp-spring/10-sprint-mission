package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @JsonIgnore
  @Column(name = "username", nullable = false, unique = true)
  private String name;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id")
  private BinaryContent profileImage;

  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY,
      cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
  private UserStatus status;

  protected User() {
  }

  public User(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
  }

  @JsonProperty("username")
  public String getUsername() {
    return name;
  }

  @JsonProperty("profileId")
  public UUID getProfileId() {
    return profileImage == null ? null : profileImage.getId();
  }

  public UUID getProfileImageId() {
    return profileImage == null ? null : profileImage.getId();
  }

  public void bindStatus(UserStatus status) {
    this.status = status;
  }

  public void updateName(String name) {
    this.name = name;
    touch();
  }

  public void updateEmail(String email) {
    this.email = email;
    touch();
  }

  public void updatePassword(String password) {
    this.password = password;
    touch();
  }

  public void updateProfileImage(BinaryContent image) {
    this.profileImage = image;
    touch();
  }

  @Override
  public String toString() {
    return "이름: " + name + "\n" + "email: " + email;
  }
}