package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;

public interface UserStatusService {

	UserStatusDto create(UserStatusCreateRequest request);

	UserStatusDto find(UUID userStatusId);

	List<UserStatusDto> findAll();

	UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

	UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

	void delete(UUID userStatusId);
}
