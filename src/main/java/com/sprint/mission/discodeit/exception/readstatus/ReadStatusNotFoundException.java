package com.sprint.mission.discodeit.exception.readstatus;

import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusNotFoundException extends ReadStatusException {

	public ReadStatusNotFoundException(UUID readStatusId) {
		super(ErrorCode.READ_STATUS_NOT_FOUND, Map.of("readStatusId", readStatusId));
	}
}