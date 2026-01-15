package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.UUID;

public interface InteractionService {
    void join(UUID userId, UUID channelId);   // 채널 참여

    void leave(UUID userId, UUID channelId);  // 채널 나가기

    void deleteUser(UUID userId);             // 유저 삭제

    void deleteChannel(UUID channelId);       // 채널 삭제
}