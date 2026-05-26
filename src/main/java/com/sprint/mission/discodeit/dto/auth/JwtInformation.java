package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.dto.user.UserDto;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtInformation {

  private final UserDto userDto;
  private String accessToken;
  private String refreshToken;
  private final Instant expiresAt;

  // 토큰 갱신 시 내부 상태를 변경하는 메서드
  public void rotate(String newAccessToken, String newRefreshToken) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
  }
}
