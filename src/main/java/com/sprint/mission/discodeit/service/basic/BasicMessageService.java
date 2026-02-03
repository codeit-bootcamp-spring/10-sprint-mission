package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
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
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UUID createMessage(UUID requesterId, CreateMessageRequest request) {
        User user = getUserOrThrow(requesterId);
        Channel channel = getChannelOrThrow(request.channelId());

        Message message = new Message(requesterId, channel.getId(), request.content());

        List<UUID> attachmentsIds = saveAttachments(request.attachments(), user.getId());
        message.updateAttachments(attachmentsIds);
        messageRepository.save(message);

        user.addMessage(message.getId());
        userRepository.save(user);

        channel.addMessage(message.getId());
        channelRepository.save(channel);

        return message.getId();
    }

    private List<UUID> saveAttachments(List<BinaryContentRequest> binaryContentRequests, UUID userId) {
        List<UUID> attachmentsIds = new ArrayList<>();

        for (BinaryContentRequest binaryContentRequest : new ArrayList<>(binaryContentRequests)) {
            BinaryContent binaryContent = new BinaryContent(
                    userId,
                    binaryContentRequest.type(),
                    binaryContentRequest.image()
            );
            attachmentsIds.add(binaryContent.getId());
            binaryContentRepository.save(binaryContent);
        }

        return attachmentsIds;
    }

    @Override
    public List<MessageResponse> findAllMessagesByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findByChannelId(channelId);

        return messages.stream().map(message -> {
            List<byte[]> images = getImagesOrThrow(message.getAttachmentIds());

            return MessageResponse.of(message, images);
        }).toList();
    }

    private List<byte[]> getImagesOrThrow(List<UUID> attachmentIds) {
        return binaryContentRepository.findImagesByIds(attachmentIds);
    }

    @Override
    public MessageResponse updateMessage(UUID requesterId, UpdateMessageRequest request) {
        Message message = getMessageOrThrow(request.messageId());

        message.validateSender(requesterId);

        message.updateContent(request.content());

        List<UUID> attachments = saveAttachments(request.attachments(), requesterId);
        message.updateAttachments(attachments);

        messageRepository.save(message);

        List<byte[]> images = getImagesOrThrow(message.getAttachmentIds());
        return MessageResponse.of(message, images);
    }

    @Override
    public void deleteMessage(UUID requestId, UUID messageId) {
        Message message = getMessageOrThrow(messageId);
        message.validateSender(requestId);

        removeMessageFromUser(message);
        removeMessageFromChannel(message);

        List<UUID> attachmentIds = message.getAttachmentIds();

        for (UUID attachmentId : attachmentIds) {
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.delete(message);
    }

    private void removeMessageFromUser(Message message) {
        User user = getUserOrThrow(message.getSenderId());
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
                .orElseThrow(() -> new IllegalArgumentException("메세지를 찾을 수 없습니다 messageId: " + messageId));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private Channel getChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다 channelId: " + id));
    }
}
