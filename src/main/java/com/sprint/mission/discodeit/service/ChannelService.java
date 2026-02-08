package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelFindResponse;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublic(ChannelCreateRequestPublic channelCreateRequestPublic);
    ChannelResponse createPrivate(ChannelCreateRequestPrivate channelCreateRequestPrivate);
    ChannelFindResponse find(UUID channelID);
    List<ChannelFindResponse> findAllByUserID(UUID userID);
    ChannelResponse updateName(UUID channelID, ChannelUpdateRequest request);
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
