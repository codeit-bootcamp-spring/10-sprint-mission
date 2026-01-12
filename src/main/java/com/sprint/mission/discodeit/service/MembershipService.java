package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface MembershipService {
    void join(UUID userId, UUID channelId);   // 유저의 채널 가입
    void leave(UUID userId, UUID channelId);  // 유저의 채널 탈퇴
    void deleteUser(UUID userId);             // 유저 삭제 (연쇄 처리 포함)
    void deleteChannel(UUID channelId);       // 채널 삭제 (연쇄 처리 포함)
}