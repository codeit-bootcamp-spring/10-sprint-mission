package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 성공 인증 응답")
public class JwtDto {

  @Schema(description = "로그인한 사용자 정보")
  private UserDto userDto;

  @Schema(description = "인증용 Access Token (Bearer)", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGFtcGxlQGdtYWlsLmNvbSIsImV4cCI6MTcxNjM5MDc3Nn0.xxxx")
  private String accessToken;
}
