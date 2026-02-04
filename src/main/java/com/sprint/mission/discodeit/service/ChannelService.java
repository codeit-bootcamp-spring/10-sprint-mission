package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public interface ChannelService {

    // Public 채널 생성 + DTO
    ChannelResponse createPublicChannel(PublicChannelCreateRequest request);
    // Private 채널 생성
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);


    //id로 채널 DTO 조회
    ChannelResponse findChannelById(UUID channelId);

    // User가 속한 channel 전체 조회 -> 채널 목록 전부!
    List<ChannelResponse> findAllByUserId (UUID userId);

    // 업데이트
    ChannelResponse updateChannel(ChannelUpdateRequest request);

    // 채널 삭제
    void deleteChannel(UUID uuid);


    // 추가 -> ChatCoordinator 분산
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
}