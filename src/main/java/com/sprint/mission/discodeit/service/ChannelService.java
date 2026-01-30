package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public interface ChannelService {

    // Public 채널 생성
    Channel createPublicChannel(PublicChannelCreateRequest request);
    // Private 채널 생성
    Channel createPrivateChannel(PrivateChannelCreateRequest request);


    // 각 서비스에 해당 ID가 존재하는지를 !
    List<Channel> getChannelAll();// channel 조회 -> 채널 목록 전부!
    Channel updateChannel(UUID uuid, String newName);// channel 정보 변경
    void deleteChannel(UUID uuid);// channel 삭제


    Channel getChannelByName(String channelName); // channel 조회 (사용자)


    //id로 채널 조회
    Channel findChannelById(UUID id);


    // 추가 ChatCoordinator 분산
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
}