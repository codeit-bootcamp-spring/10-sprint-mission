package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDTO;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 공개 채널 생성
    ChannelResponseDTO createPublicChannel(PublicChannelCreateRequestDTO publicChannelCreateRequestDTO);

    // 비공개 채널 생성
    ChannelResponseDTO createPrivateChannel(PrivateChannelCreateRequestDTO privateChannelCreateRequestDTO);

    // 채널 단건 조회
    Channel searchChannel(UUID targetChannelId);

    // 채널 전체 조회
    List<Channel> searchChannelAll();

    // 특정 사용자가 속한 채널 목록 반환
    List<Channel> searchChannelsByUserId(UUID userId);

    // 채널 수정
    Channel updateChannel(UUID targetChannelId, String newChannelName);

    // 채널 저장
    void updateChannel(UUID id, Channel channel);

    // 채널 삭제
    void deleteChannel(UUID targetChannelId);
}