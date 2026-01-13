package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(Channel channel, User sender, String text);
    Message read(UUID id);
    List<Message> readAll();
    List<Message> readAllMessageByChannel(Channel channelId); // 채널별 메시지 목록 조회
    void update(UUID id, String text);
    void delete(UUID id);

}
