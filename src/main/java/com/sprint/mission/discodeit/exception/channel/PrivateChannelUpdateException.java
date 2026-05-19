package com.sprint.mission.discodeit.exception.channel;

import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelUpdateException extends ChannelException {

	public PrivateChannelUpdateException(UUID channelId) {
		super(ErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED, Map.of("channelId", channelId));
	}
}