package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channeldto.ChannelViewDTO;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.UpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelCreateDTO req);
    Channel createPrivateChannel(PrivateChannelCreateDTO req);
    ChannelViewDTO find(UUID channelId);
    List<ChannelViewDTO> findAllByUserId(UUID userID);
    Channel join(UUID channelId, UUID userId);
    Channel leave(UUID channelId, UUID userId);
    Channel update(UpdateRequestDTO req);
    void delete(UUID channelId);
}
