package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: channelId와 owner 기타 등등 출력
    Channel createChannel(UUID ownerId, ChannelType channelType, String channelName, String channelDescription);

    // R. 읽기
    // 특정 채널 정보 읽기
    Optional<Channel> findChannelById(UUID channelId);

    // R. 모두 읽기
    // 채널 목록 전체
    List<Channel> findAllChannels();
    // 비공개 여부에 따른 채널 목록
    List<Channel> findPublicOrPrivateChannel(ChannelType channelType);
    // 특정 채널에 속한 모든 유저
    List<UUID> findAllUsersByChannelId(UUID channelId);
    // 특정 사용자가 owner인 모든 채널
    List<Channel> findOwnerChannelsByUserId(UUID userId);
    // 특정 채널의 모든 메시지 읽어오기
    List<Message> readChannelMessageByChannelId(UUID channelId);
    // 특정 채널에서 원하는 메시지 찾기
    List<Message> searchChannelMessageByChannelIdAndWord(UUID channelId, String partialWord);

    // U. 수정
    Channel updateChannelInfo(UUID ownerId, UUID channelId, ChannelType channelType, String channelName, String channelDescription);
    // 채널 owner 변경
    Channel changeChannelOwner(UUID requestUserId, UUID channelId, UUID newOwnerId);
    // 채널 참여하기
    Channel joinChannel(UUID userId, UUID channelId);
    // 채널 나가기
    Channel leaveChannel(UUID userId, UUID channelId);



    // D. 삭제
    void deleteChannel(UUID requestId, UUID channelId);
}
