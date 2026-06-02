package com.sprint.mission.discodeit.auth.jwt;

import com.sprint.mission.discodeit.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtInformation {

  private UserDto.Response userDto;
  private String accessToken;
  private String refreshToken;

  public void rotate(String newAccessToken, String newRefreshToken) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;

  }
}
