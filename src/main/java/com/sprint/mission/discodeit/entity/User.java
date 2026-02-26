package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User extends Base {

  private final List<UUID> channelIds; // 특정 유저의 채널 소속
  private final List<UUID> messageIds; // 특정 유저가 생성한 모든 메시지

  private UUID profileId; // 프로필 이미지의 id
  @Deprecated
  private String nickName; // 일단 지금은 안 쓰는듯
  private String username;
  private String email;
  private String phoneNumber;
  private String password;

  public User(String nickName, String username, String email, String phoneNumber, String password) {
    this.nickName = nickName;
    this.username = username;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.password = password;

    this.channelIds = new ArrayList<>();
    this.messageIds = new ArrayList<>();
  }

  public void updateProfileId(UUID profileId) {
    this.profileId = profileId;
    updateUpdatedAt(Instant.now());
  }

  public void updateNickName(String nickName) {
    this.nickName = nickName;
    updateUpdatedAt(Instant.now());
  }

  public void updateUsername(String username) {
    this.username = username;
    updateUpdatedAt(Instant.now());
  }

  public void updateEmail(String email) {
    this.email = email;
    updateUpdatedAt(Instant.now());
  }

  public void updatePhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    updateUpdatedAt(Instant.now());
  }

  public void updatePassword(String password) {
    this.password = password;
    updateUpdatedAt(Instant.now());
  }

  public void addChannelId(UUID channelId) {
    this.channelIds.add(channelId);
  }

  public void addMessageId(UUID messageId) {
    this.messageIds.add(messageId);
  }

  @Override
  public String toString() {
    return "{" +
        nickName + "(" + username + ") / " +
        "newEmail: " + email + " / " +
        "newPhoneNumber: " + phoneNumber +
        "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User user)) {
      return false;
    }
    return Objects.equals(getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
