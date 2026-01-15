package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 생성
    User createUser(String userName);

    // 읽기
    User findById(UUID id);

    // 모두 읽기
    List<User> findAll();

    // 수정
    User updateById(UUID id, String newUserName);

    // 삭제
    void deleteById(UUID id);

    // 해당 channel Id를 가진 유저 목록을 반환
    List<User> getUsersByChannelId(UUID channelId);

    void setChannelService(ChannelService channelService);

    void setMessageService(MessageService messageService);
}
