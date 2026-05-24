package com.sprint.mission.discodeit.userstatus.service;

import com.sprint.mission.discodeit.userstatus.dto.UserStatusDto;
import com.sprint.mission.discodeit.userstatus.dto.request.UserStatusUpdateRequest;
import java.util.UUID;

public interface UserStatusService {

  UserStatusDto update(UUID userId, UserStatusUpdateRequest request);
}
