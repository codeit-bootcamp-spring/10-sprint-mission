package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 채널 생성
    public Channel createChannel(String channelName, UUID userId, ChannelType channelType);

    // 채널 단건 조회
    public Channel searchChannel(UUID targetChannelId);

    // 채널 전체 조회
    public List<Channel> searchChannelAll();

    // 채널 수정
    public Channel updateChannel(UUID targetChannelId, String newChannelName);

    // 채널 삭제
    public void deleteChannel(UUID targetChannelId);
}