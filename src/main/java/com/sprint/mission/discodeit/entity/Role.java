package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public enum Role {
  ADMIN("ADMIN", "ROLE_ADMIN"),
  CHANNEL_MANAGER("CHANNEL_MANAGER", "ROLE_CHANNEL_MANAGER"),
  USER("USER", "ROLE_USER");

  private final String dbKey;
  private final String authority;

  Role(String dbKey, String authority) {
    this.dbKey = dbKey;
    this.authority = authority;
  }

  public static Role fromString(String roleStr) {
    if (roleStr == null) {
      return Role.USER;
    }
    try {
      return Role.valueOf(roleStr.toUpperCase().trim());
    } catch (IllegalArgumentException e) {
      return Role.USER;
    }
  }
}
