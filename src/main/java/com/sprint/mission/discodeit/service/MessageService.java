package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 메시지 생성 (Message 객체 직접 전달)
    Message createMessage(User sender, Channel channel, String content);

    // 단건 조회 (메시지 ID 기준)
    Message findMessage(UUID messageId);

    // 채널별 메시지 조회
    List<Message> findAllByChannelMessage(UUID channelId);

    // 서버 전체 메시지 조회
    List<Message> findAllMessage();


    public List<Message> findAllByUserMessage(UUID userId);

    // 메시지 수정
    Message updateMessage(UUID messageId, String newContent);

    // 메시지 삭제
    void deleteMessage(UUID messageId);
}
