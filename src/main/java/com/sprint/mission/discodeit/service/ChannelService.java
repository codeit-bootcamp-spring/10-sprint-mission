package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.listener.ChannelLifecycleListener;

import java.util.*;

public interface ChannelService {
    // 채널 생성
    Channel createChannel(String channelName, User owner);
    // 채널 조회
    Channel findChannelById(UUID channelId);
    // 채널 전체 조회
    List<Channel> findAllChannels();
//    Channel findChannelByName(String channelName);
//    Channel findChannelByOwner(User owner);
    // 채널 이름 수정
    Channel updateChannel(UUID channelId, String newChannelName);
    // 채널 삭제
    void deleteChannel(UUID channelId);
    // OWNER가 소유하고 있는 모든 채널 삭제
    void deleteChannelsByOwnerId(UUID ownerId);

    void addListener(ChannelLifecycleListener listener);
}