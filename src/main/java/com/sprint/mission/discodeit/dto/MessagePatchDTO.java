package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record MessagePatchDTO(
	UUID messageId,
	String text
) {

}
