package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: channelId와 owner 기타 등등 출력
    Channel createPublicChannel(PublicChannelCreateRequest request);
    Channel createPrivateChannel(PrivateChannelCreateRequest request);

    // R. 읽기
    // 특정 채널 정보 읽기
    ChannelResponse findChannelById(UUID channelId);

    // R. 모두 읽기
    // 채널 목록 전체
    List<ChannelResponse> findAllByUserId(UUID userId);
    // 특정 사용자가 참여한 모든 channel
    List<UUID> findJoinChannelsByUserId(UUID userId);
//    // 비공개 여부에 따른 채널 목록
//    List<Channel> findPublicOrPrivateChannel(ChannelType channelType);
//    // 특정 사용자가 owner인 모든 채널
//    List<Channel> findOwnerChannelsByUserId(UUID userId);

    // U. 수정
    Channel updateChannelInfo(ChannelUpdateRequest request);
    // 채널 owner 변경
    Channel changeChannelOwner(UUID currentUserId, UUID channelId, UUID newOwnerId);
    // 채널 참여하기
    Channel joinChannel(UUID userId, UUID channelId);
    // 채널 나가기
    Channel leaveChannel(UUID userId, UUID channelId);

    // D. 삭제
    void deleteChannel(UUID ownerId, UUID channelId);
}
