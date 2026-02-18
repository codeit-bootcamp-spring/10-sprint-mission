package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessagePostDto(
	UUID authorId,
	UUID channelId,
	String text,
	List<BinaryContentDto> attachments
) {
}
