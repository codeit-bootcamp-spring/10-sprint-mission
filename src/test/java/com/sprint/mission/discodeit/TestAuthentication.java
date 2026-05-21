package com.sprint.mission.discodeit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.UUID;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;

public final class TestAuthentication {

	private static final String TEST_PASSWORD = "Password1!";

	private TestAuthentication() {
	}

	public static RequestPostProcessor authenticated(UserDto userDto) {
		return authenticated(userDto, userDto.role());
	}

	public static RequestPostProcessor authenticated(UserDto userDto, Role role) {
		return user(new DiscodeitUserDetails(toPrincipalUser(userDto, role), TEST_PASSWORD));
	}

	public static RequestPostProcessor authenticated(UUID userId) {
		return authenticated(new UserDto(
			userId,
			"test-user-" + userId,
			"test-user-" + userId + "@example.com",
			null,
			true,
			Role.USER
		));
	}

	private static UserDto toPrincipalUser(UserDto userDto, Role role) {
		return new UserDto(
			userDto.id(),
			userDto.username(),
			userDto.email(),
			userDto.profile(),
			true,
			role
		);
	}
}
