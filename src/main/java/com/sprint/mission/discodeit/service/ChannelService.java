package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 채널 생성 (pri / pub)
    UUID createPublic(
            PublicChannelCreateRequest publicChannelCreateRequest);
    UUID createPrivate(
            PrivateChannelCreateRequest privateChannelCreateRequest);
    // 특정 채널 조회
    ChannelResponse find(UUID channelId);
    // 전체 채널 목록 조회
    List<ChannelResponse> findAllByUserId(UUID userId);
    // 채널에 사용자 추가
    Channel addUserInChannel(UUID channelId, UUID userId);
    // 채널이름 변경
    Channel update(ChannelUpdateRequest channelUpdateRequest);
    // 채널 삭제
    void delete(UUID channelId);
    // 채널에 사용자 삭제
    void removeUserFromChannel(UUID channelId, UUID userId);
    // **특정 채널 참가자 목록 조회**
    List<UUID> findAllUserInChannel(UUID channelId);
    boolean existsById(UUID channelId);
}