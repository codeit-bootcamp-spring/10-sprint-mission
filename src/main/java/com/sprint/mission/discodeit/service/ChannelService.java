package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 생성
    Channel createChannel(String channelName);

    // 읽기
    Channel findById(UUID id);

    // 모두 읽기
    List<Channel> findAll();

    // 수정
    Channel updateById(UUID id, String newChannelName);

    // 삭제
    void deleteById(UUID id);

    // 해당 id를 가진 채널의 메시지 목록을 반환
    List<Message> getMessagesById(UUID id);

    // 해당 id를 가진 채널의 유저 목록을 반환
    List<User> getUsersById(UUID id);
}
