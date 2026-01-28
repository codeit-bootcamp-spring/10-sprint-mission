package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public interface BasicMessageService {
    Message createMessage(UUID userId, String content, UUID channelId);

    Message findById(UUID id);

    List<Message> findAll();

    Message updateById(UUID id, String content);

    void deleteById(UUID id);

    // 해당 user Id를 가진 유저가 작성한 메시지 목록을 반환
    List<Message> getMessagesByUserId(UUID userId);

    // 해당 channel Id를 가진 채널의 메시지 목록을 반환
    List<Message> getMessagesByChannelId(UUID channelId);

    // setter
    void setUserService(BasicUserService userService);
    void setChannelService(BasicChannelService channelService);
    void setUserRepository(UserRepository userRepository);
    void setChannelRepository(ChannelRepository channelRepository);

}
