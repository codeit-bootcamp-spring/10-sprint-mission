package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateInfo;
import com.sprint.mission.discodeit.dto.message.MessageInfo;
import com.sprint.mission.discodeit.dto.message.MessageUpdateInfo;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public final class MessageMapper {
    private MessageMapper(){}

    public static MessageInfo toMessageInfo(Message message) {
        return new MessageInfo(
                message.getId(),
                message.getContent(),
                message.getSenderId(),
                message.getChannelId(),
                message.getAttachmentIds()
        );
    }

    public static MessageCreateInfo toMessageCreateInfo(
            String content,
            UUID senderId,
            UUID channelId,
            List<byte[]> attachments
    ) {
        return new MessageCreateInfo(
                content,
                senderId,
                channelId,
                attachments
        );
    }

    public static MessageUpdateInfo toMessageUpdateInfo(
            UUID messageId,
            String content
    ) {
        return new MessageUpdateInfo(
                messageId,
                content
        );
    }
}
