package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

// channelData 자체를 다루는 걸 저장 로직이라고 보면 된다
public interface ChannelRepository {
    Channel find(UUID channelID);
    List<Channel> findAll();
    void addChannel(Channel channel);
    void removeChannel(Channel channel);
    Channel save(Channel channel);
}
