package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.message.dto.MessageCreateInfo;
import com.sprint.mission.discodeit.message.dto.MessageInfo;
import com.sprint.mission.discodeit.message.dto.MessageUpdateInfo;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageInfo createMessage(MessageCreateInfo createInfo);
    MessageInfo findMessage(UUID messageId);
    List<MessageInfo> findAll();
    List<MessageInfo> findAllByUserId(UUID userId);
    List<MessageInfo> findAllByChannelId(UUID channelId);
    MessageInfo updateMessage(UUID messageId, MessageUpdateInfo messageInfo);
    void deleteMessage(UUID messageId);
}
