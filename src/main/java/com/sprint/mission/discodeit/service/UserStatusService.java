package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import java.util.UUID;

public interface UserStatusService {

  UserStatusDto update(UUID userId, UserStatusUpdateRequest request);
}
