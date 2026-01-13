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

    // 해당 id를 가진 유저가 속한 채널 목록을 반환
    List<Channel> getChannelsById(UUID id);

    // 해당 id를 가진 유저가 작성한 메시지 목록을 반환
    List<Message> getMessagesById(UUID id);
}
