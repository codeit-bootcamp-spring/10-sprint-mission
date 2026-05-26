package com.sprint.mission.discodeit.dto.authDto.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResultDto {
    String newAccessToken;
    String newRefreshToken;
    String email;
}
