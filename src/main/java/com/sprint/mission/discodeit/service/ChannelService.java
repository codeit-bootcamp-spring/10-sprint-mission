package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: channelId와 owner 기타 등등 출력
    Channel createChannel(User owner, Boolean isPrivate, String channelName, String channelDescription);

    // R. 읽기
    // 채널 하나
    Channel readChannelByChannelId(UUID channelId);
    Channel readChannelByChannelName(String channelName);

    // R. 모두 읽기
    // 채널 목록 전체
    List<Channel> readAllChannel();
    // 공개 채널 전체
    List<Channel> readAllPublicChannel(Boolean isPrivate);
    // 특정 사용자가 참여한 모든 채널
    List<Channel> readAllChannelsAtUserByUserId(UUID userId);
    // 특정 사용자가 참여한 채널 중에서 특정 채널 검색
    List<Channel> searchAllChannelsAtUserByUserIdAndPartialChannelName(UUID userId, String partialChannelName);

    // U. 수정
    // 채널 channelName 수정
    Channel updateChannelName(UUID channelId, String channelName);
    // 채널 isPrivate 수정
    Channel updateChannelIsPrivate(UUID channelId, Boolean isPrivate);
    // 채널 owner 수정
    Channel updateChannelOwner(UUID channelId, User owner);
    // 채널 description 수정
    Channel updateChannelDescription(UUID channelId, String channelDescription);

    // D. 삭제
    void deleteChannel(UUID channelId);
}
