package com.sprint.mission.discodeit.auth.dto;

import com.sprint.mission.discodeit.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtInformation {

  private final UserDto userDto;
  private String accessToken;
  private String refreshToken;

  public void rotate(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
