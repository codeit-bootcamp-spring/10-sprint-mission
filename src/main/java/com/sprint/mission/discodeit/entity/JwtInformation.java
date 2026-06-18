package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtInformation extends BaseEntity {

  public JwtInformation(UserDto userDto, String accessToken, String refreshToken) {
    this.userDto = userDto;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  private UserDto userDto;
  private String accessToken;
  private String refreshToken;

  public JwtInformation() {

  }

  public void rotate(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

}
