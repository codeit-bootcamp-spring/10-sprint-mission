package com.sprint.mission.discodeit.dto.auth;

public record JwtRefreshResult(
	JwtDto jwtDto,
	String refreshToken
) {
}
