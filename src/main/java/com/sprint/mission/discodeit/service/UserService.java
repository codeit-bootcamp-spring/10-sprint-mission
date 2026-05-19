package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

public interface UserService {

	UserDto create(UserCreateRequest userCreateRequest,
		Optional<BinaryContentCreateRequest> profileCreateRequest);

	UserDto find(UUID userId);

	List<UserDto> findAll();

	UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
		Optional<BinaryContentCreateRequest> profileCreateRequest);

	@PreAuthorize("hasRole('ADMIN')")
	UserDto updateRole(UserRoleUpdateRequest request);

	void delete(UUID userId);
}
