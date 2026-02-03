package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.ChannelPatchDTO;
import com.sprint.mission.discodeit.dto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDTO;
import com.sprint.mission.discodeit.dto.PublicChannelPostDTO;
import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelService {
	Channel createPublicChannel(PublicChannelPostDTO publicChannelPostDTO);

	Channel createPrivateChannel(PrivateChannelPostDTO channelPostDTO);

	ChannelResponseDTO findById(UUID channelId);

	List<ChannelResponseDTO> findAllByUserId(UUID userId);

	Channel updateName(ChannelPatchDTO channelPatchDTO);

	Channel addUser(UUID channelId, UUID userId);

	boolean deleteUser(UUID channelId, UUID userId);

	void delete(UUID channelId);

	boolean isUserInvolved(UUID channelId, UUID userId);
}
