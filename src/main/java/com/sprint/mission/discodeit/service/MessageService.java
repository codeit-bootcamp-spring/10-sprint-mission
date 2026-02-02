package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateInfo;
import com.sprint.mission.discodeit.dto.message.MessageInfo;
import com.sprint.mission.discodeit.dto.message.MessageUpdateInfo;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageInfo createMessage(MessageCreateInfo createInfo);
    MessageInfo findMessage(UUID messageId);
    List<MessageInfo> findAll();
    List<MessageInfo> findAllByUserId(UUID userId);
    List<MessageInfo> findAllByChannelId(UUID channelId);
    MessageInfo updateMessage(MessageUpdateInfo messageInfo);
    void deleteMessage(UUID messageId);
}
