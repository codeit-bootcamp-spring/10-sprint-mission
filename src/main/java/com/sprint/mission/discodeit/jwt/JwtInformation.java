package com.sprint.mission.discodeit.jwt;

import com.sprint.mission.discodeit.user.dto.UserDto;

public record JwtInformation(
    UserDto userDto,
    String accessToken,
    String refreshToken
) {

  public JwtInformation rotate(String accessToken, String refreshToken) {
    return new JwtInformation(this.userDto, accessToken, refreshToken);
  }

}
