package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;

import java.util.UUID;
import java.util.List;


public interface ChannelService {
    ChannelResponse createPublicChannel(PublicChannelCreateRequest request); // PUBLIC 채널 생성

    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request); // PRIVATE 채널 생성

    ChannelResponse findById(UUID id); // 단건 조회

    List<ChannelResponse> findAllByUserId(UUID userId); // 유저별 참여하고 있는 채널 전체 조회

    ChannelResponse update(UUID id, ChannelUpdateRequest request); // 체널 정보 수정

    void deleteById(UUID id);
}