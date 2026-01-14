package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String title, String description);
    // get: uuid로 검색하는건 확실하게 Channel반환 or Throw
    Channel getChannel(UUID uuid);
    // find: 그 외의 필드로 검색하는건 Optional<Channel>로 호출한 쪽에서 분기처리
    Optional<Channel> findChannelByTitle(String title);
    List<Channel> findAllChannels();
    Channel updateChannel(UUID uuid, String title, String description);
    void deleteChannel(UUID uuid);
    void deleteChannelByTitle(String title);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
}
