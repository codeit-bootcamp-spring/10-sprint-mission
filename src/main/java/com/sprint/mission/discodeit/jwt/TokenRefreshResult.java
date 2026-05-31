package com.sprint.mission.discodeit.jwt;

public record TokenRefreshResult(
    String accessToken,
    String refreshToken
) {

}
