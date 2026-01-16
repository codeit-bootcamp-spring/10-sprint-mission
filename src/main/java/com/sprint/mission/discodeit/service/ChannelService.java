package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;
import java.util.List;


public interface ChannelService {
    Channel create(String name, String description, String type, boolean isPublic);

    Channel findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, String name, String description, boolean isPublic);

    void delete(UUID id);

    void join(UUID channelId, UUID userId); // 채널 참가

    void leave(UUID channelId, UUID userId); // 채널 탈퇴

    List<User> findMembersByChannelId(UUID channelId); // 특정 채널의 유저 목록 조회

    List<Message> findMessagesByChannelId(UUID channelId); // 특정 채널의 메시지 목록 조회
}