package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 1. 메시지 생성
    Message messageCreate(UUID channelId, User sender, String content);

    // 2. 메시지 단건 조회
    Message messageFind(UUID channelId, UUID messageId);

    // 3. 메시지 전체 조회
    List<Message> messageFindAll(UUID channelId);

    // 4. 메시지 수정
    Message messageUpdate(UUID channelId, UUID messageId, String newContent);

    // 5. 메시지 삭제
    void messageDelete(UUID channelId, UUID messageId);
}
