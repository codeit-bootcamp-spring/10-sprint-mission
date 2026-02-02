package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    ChannelDto.response createChannel(ChannelDto.createPrivateRequest channelPrivateReq);
    ChannelDto.response createChannel(ChannelDto.createPublicRequest channelPublicReq);
    // get: uuid로 검색하는건 확실하게 Channel반환 or Throw
    ChannelDto.response getChannel(UUID uuid);
    // find: 그 외의 필드로 검색하는건 Optional<Channel>로 호출한 쪽에서 분기처리
    Optional<ChannelDto.response> findChannelByTitle(String title);
    List<ChannelDto.response> findAllByUserId(UUID userId);
    ChannelDto.response updateChannel(UUID uuid, ChannelDto.updatePublicRequest channelReq);
    void deleteChannel(UUID uuid);

    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
}
