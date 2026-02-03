package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ChannelPatchDTO(
	UUID channelId,
	String name,
	String description
) {
}
