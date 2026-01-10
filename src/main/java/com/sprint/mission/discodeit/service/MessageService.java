package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: messageID과 내용 출력
    Message createMessage(Channel channel, User author, String content);

    // R. 읽기
    Message readMessageById(UUID messageId);

    // R. 모두 읽기 : 시간순으로 정렬?
    // 메시지 전체
    List<Message> readAllMessage();
    // 특정 채널의 모든 메시지 읽어오기
    List<Message> readAllMessageAtChannelByChannelId(UUID channelId);
    // 특정 유저의 모든 메시지 읽어오기
    List<Message> readAllMessageAtUserByUserIdAndMessageId(UUID userId);
    // 특정 채널에서 원하는 메시지 찾기
    List<Message> searchAllMessageAtChannelByChannelIdAndWord(UUID channelId, String partialWord);

    // U. 수정
    // 메시지 수정
    Message updateMessageContent(UUID messageId, String content);

    // D. 삭제
    void deleteMessage(UUID messageId);
}
