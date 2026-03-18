package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
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
  private String username;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
  @JoinColumn(name = "profile_id")
  private BinaryContent profileImage;

  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY,
      cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus status;

  protected User() {
  }

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  @JsonProperty("username")
  public String getUsername() {
    return username;
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

  public void updateName(String username) {
    this.username = username;
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
    return "이름: " + username + "\n" + "email: " + email;
  }
}