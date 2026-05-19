package com.sprint.mission.discodeit.exception.readstatus;

import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

	public ReadStatusAlreadyExistsException(UUID userId, UUID channelId) {
		super(ErrorCode.READ_STATUS_ALREADY_EXISTS,
			Map.of("userId", userId, "channelId", channelId));
	}
}