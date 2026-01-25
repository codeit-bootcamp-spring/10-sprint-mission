package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.List;
import java.util.UUID;

public interface BasicUserService {
    // 유저 생성
    User createUser(String userName);

    // 읽기
    User findById(UUID id);

    // 모두 읽기
    List<User> findAll();

    // 수정
    User updateById(UUID id, String userName);

    // 삭제
    void deleteById(UUID id);

    // 해당 channel id를 가진 유저 목록을 반환
    List<User> getUsersByChannelId(UUID channelId);

    // 채널 입장
    void joinChannel(UUID userId, UUID channelId);

    // setter
    void setChannelService(BasicChannelService channelService);
    void setMessageService(BasicMessageService messageService);
    void setChannelRepository(ChannelRepository channelRepository);
    void setMessageRepository(MessageRepository messageRepository);


}
