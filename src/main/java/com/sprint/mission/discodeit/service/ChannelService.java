package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String channelName);
    Channel findById(UUID id);
    List<Channel> findAll();

    // 메서드 이름, 매개변수 이름은 추후 필드가 확장되면 변경 예정 (다른 도메인 메서드 또한 동일)
    // Ex: 채널 이름 변경 - updateChannelName, 채널 소개글 변경 - updateChannelIntroduce
    Channel update(UUID id, String updateChannelName);
    void delete(UUID id);

    void joinUser(UUID channelId, UUID userId);
    void leaveUser(UUID channelId, UUID userId);
}
