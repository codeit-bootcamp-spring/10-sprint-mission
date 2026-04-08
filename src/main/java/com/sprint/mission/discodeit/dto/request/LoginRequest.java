package com.sprint.mission.discodeit.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginRequest {

  @NotEmpty
  private final String username;

  @NotEmpty
  private final String password;

  public LoginRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
