package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.response.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentService binaryContentService;

    @Override
    public Message createMessage(UUID requesterId, MessageCreateRequest request,
                                 List<BinaryContentRequest> attachments) {
        User user = getUserOrThrow(requesterId);
        Channel channel = getChannelOrThrow(request.channelId());

        Message message = new Message(requesterId, channel.getId(), request.content());

        List<UUID> attachmentsIds = saveAttachments(attachments, user.getId());
        message.updateAttachments(attachmentsIds);
        messageRepository.save(message);

        user.addMessage(message.getId());
        userRepository.save(user);

        channel.addMessage(message.getId());
        channelRepository.save(channel);

        return message;
    }

    private List<UUID> saveAttachments(List<BinaryContentRequest> binaryContentRequests, UUID userId) {
        if (binaryContentRequests == null) {
            return List.of();
        }

        List<UUID> attachmentsIds = new ArrayList<>();

        for (BinaryContentRequest binaryContentRequest : new ArrayList<>(binaryContentRequests)) {
            UUID binaryContentId = binaryContentService.createBinaryContent(userId, binaryContentRequest);
            attachmentsIds.add(binaryContentId);
        }

        return attachmentsIds;
    }

    @Override
    public List<Message> findAllMessagesByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message updateMessage(UUID messageId, MessageUpdateRequest request) {
        Message message = getMessageOrThrow(messageId);

        message.updateContent(request.newContent());

        messageRepository.save(message);

        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = getMessageOrThrow(messageId);

        removeMessageFromUser(message);
        removeMessageFromChannel(message);

        List<UUID> attachmentIds = message.getAttachmentIds();

        for (UUID attachmentId : attachmentIds) {
            binaryContentService.deleteBinaryContent(attachmentId);
        }

        messageRepository.delete(message);
    }

    private void removeMessageFromUser(Message message) {
        User user = getUserOrThrow(message.getAuthorId());
        user.removeMessage(message.getId());
        userRepository.save(user);
    }

    private void removeMessageFromChannel(Message message) {
        Channel channel = getChannelOrThrow(message.getChannelId());
        channel.removeMessage(message.getId());
        channelRepository.save(channel);
    }

    private Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND,
                        "메세지를 찾을 수 없습니다 messageId: " + messageId));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private Channel getChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHANNEL_NOT_FOUND,
                        "채널을 찾을 수 없습니다 channelId: " + id));
    }
}
