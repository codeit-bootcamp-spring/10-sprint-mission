package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void setMessageService(MessageService messageService);
    void setUserService(UserService userService);
    Channel create(String name);
    Channel find(UUID id);
    List<Channel> findAll();
    Channel updateName(UUID channelID, String name);
    default void update() {}
    // channel 자체 삭제
    void deleteChannel(UUID channelID);
    // channel에 user 가입
    void joinChannel (UUID userID, UUID channelID);
    // channel에 user 탈퇴
    void leaveChannel(UUID userID, UUID channelID);
    // channel에서 userList 반환
    List<String> findMembers(UUID channelID);
}
