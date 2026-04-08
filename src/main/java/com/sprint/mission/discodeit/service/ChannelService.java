package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelPatchDto;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDto;
import com.sprint.mission.discodeit.dto.PublicChannelPostDto;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelDto createPublicChannel(PublicChannelPostDto publicChannelPostDTO);

    ChannelDto createPrivateChannel(PrivateChannelPostDto channelPostDTO);

    ChannelDto findById(UUID channelId);

    List<ChannelDto> findAllByUserId(UUID userId);

    ChannelDto update(UUID channelId, ChannelPatchDto channelPatchDTO);

    ChannelDto addUser(UUID channelId, UUID userId);

    boolean deleteUser(UUID channelId, UUID userId);

    void delete(UUID channelId);

}
