package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublic(ChannelCreateRequestPublic channelCreateRequestPublic);
    Channel createPrivate(ChannelCreateRequestPrivate channelCreateRequestPrivate);
    ChannelResponse find(UUID id);
    List<ChannelResponse> findAllByUserID(UUID userID);
    Channel updateName(UUID channelID, String name);
    default void update() {}
    // channel 자체 삭제
    void deleteChannel(UUID channelID);
    // channel에 user 가입
    void joinChannel (UUID userID, UUID channelID);
    // channel에 user 탈퇴
    void leaveChannel(UUID userID, UUID channelID);
    // channel에서 userList 반환
    List<String> findMembers(UUID channelID);
}
