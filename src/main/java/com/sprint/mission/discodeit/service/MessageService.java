package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.input.MessageCreateInput;
import com.sprint.mission.discodeit.dto.message.input.MessageUpdateInput;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: messageID과 내용 출력
    Message createMessage(MessageCreateInput request);

    // R. 읽기
    // 특정 메시지 정보 읽기
    Message findMessageById(UUID messageId);

    // R. 모두 읽기 : 시간순으로 정렬?
    // 메시지 전체
    List<Message> findAllMessages();
    // 특정 채널의 모든 메시지 읽어오기
    List<MessageResponse> findAllByChannelId(UUID channelId);
    // 특정 사용자가 작성한 모든 메시지
    List<Message> findUserMessagesByUserId(UUID userId);

    // U. 수정
    // 메시지 수정
    Message updateMessageContent(MessageUpdateInput request);

    // D. 삭제
    void deleteMessage(UUID userId, UUID messageId);
}
