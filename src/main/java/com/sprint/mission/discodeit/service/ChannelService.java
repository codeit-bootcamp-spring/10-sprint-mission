package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 공개 채널 생성
    ChannelResponseDTO createPublicChannel(PublicChannelCreateRequestDTO publicChannelCreateRequestDTO);

    // 비공개 채널 생성
    ChannelResponseDTO createPrivateChannel(PrivateChannelCreateRequestDTO privateChannelCreateRequestDTO);

    // 채널 단건 조회
    ChannelResponseDTO findById(UUID targetChannelId);

    // 채널 전체 조회
    List<ChannelResponseDTO> findAll();

    // 특정 사용자가 속한 채널 목록 반환
    List<ChannelResponseDTO> findAllByUserId(UUID userId);

    // 채널 수정
    ChannelResponseDTO update(UUID channelId, ChannelUpdateRequestDTO channelUpdateRequestDTO);

    // 채널 삭제
    void delete(UUID targetChannelId);

    // 채널 참가자 초대
    void inviteMember(ChannelMemberRequestDTO channelMemberRequestDTO);

    // 채널 퇴장
    void leaveMember(ChannelMemberRequestDTO channelMemberRequestDTO);
}