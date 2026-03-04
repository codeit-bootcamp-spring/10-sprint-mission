package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channeldto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.ChannelUpdateRequestDTO;

import com.sprint.mission.discodeit.dto.channeldto.PublicChannelUpdateRequestDTO;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponseDTO createPublicChannel(PublicChannelCreateDTO req);

    ChannelResponseDTO createPrivateChannel(PrivateChannelCreateDTO req);

    ChannelResponseDTO find(UUID channelId);

    List<ChannelResponseDTO> findAllByUserId(UUID userID);

    ChannelResponseDTO join(UUID channelId, UUID userId);

    ChannelResponseDTO leave(UUID channelId, UUID userId);

    ChannelResponseDTO update(UUID channelId, PublicChannelUpdateRequestDTO req);

    void delete(UUID channelId);
}
