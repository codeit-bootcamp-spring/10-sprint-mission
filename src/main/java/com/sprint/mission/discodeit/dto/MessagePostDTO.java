package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessagePostDTO(
	UUID userId,
	UUID channelId,
	String text,
	List<BinaryContentDTO> attachments
) {
}
