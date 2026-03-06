package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;

import com.sprint.mission.discodeit.dto.channeldto.PublicChannelUpdateRequestDTO;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelDto createPublicChannel(PublicChannelCreateDTO req);

    ChannelDto createPrivateChannel(PrivateChannelCreateDTO req);

    ChannelDto find(UUID channelId);

    List<ChannelDto> findAllByUserId(UUID userID);

    ChannelDto update(UUID channelId, PublicChannelUpdateRequestDTO req);

    void delete(UUID channelId);
}
