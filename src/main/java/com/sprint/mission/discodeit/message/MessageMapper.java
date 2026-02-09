package com.sprint.mission.discodeit.message;

import com.sprint.mission.discodeit.message.dto.MessageCreateInfo;
import com.sprint.mission.discodeit.message.dto.MessageInfo;
import com.sprint.mission.discodeit.message.dto.MessageUpdateInfo;

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
