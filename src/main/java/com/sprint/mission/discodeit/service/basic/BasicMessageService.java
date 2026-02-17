package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public MessageDto create(UUID channelId,
                             UUID authorId,
                             MessageCreateRequest messageCreateRequest) {
        validateMember(channelId, authorId);

        List<BinaryContentCreateRequest> attachmentRequests = messageCreateRequest.binaryContentCreateRequests();
        if (attachmentRequests == null) {
            attachmentRequests = List.of();
        }

        List<UUID> attachmentIds = attachmentRequests.stream()
                .filter(Objects::nonNull)
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
                    return createdBinaryContent.getId();
                })
                .toList();

        String content = messageCreateRequest.content();
        Message message = new Message(content, channelId, authorId, attachmentIds);
        messageRepository.save(message);

        return toDto(message);
    }

    @Override
    public MessageDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND));
        return toDto(message);
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId, UUID userId) {
        validateMember(channelId, userId);

        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MessageDto update(UUID channelId,
                             UUID authorId,
                             UUID messageId,
                             MessageUpdateRequest messageUpdateRequest) {
        Message message = validateMessage(channelId, authorId, messageId);

        message.update(messageUpdateRequest.newContent());
        messageRepository.save(message);

        return toDto(message);
    }

    @Override
    public void delete(UUID channelId, UUID authorId, UUID messageId) {
        Message message = validateMessage(channelId, authorId, messageId);

        message.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }

    private MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }

    private Message validateMessage(UUID channelId, UUID authorId, UUID messageId) {
        if (!channelRepository.existsById(channelId)) {
            throw new BusinessException(ErrorCode.CHANNEL_NOT_FOUND);
        }
        if (!userRepository.existsById(authorId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND));

        if (!message.getChannelId().equals(channelId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (!message.getAuthorId().equals(authorId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return message;
    }

    private void validateMember(UUID channelId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

        if (channel.getType() == ChannelType.PUBLIC) {
            return;
        }

        if (readStatusRepository.findByChannelIdAndUserId(channelId, userId).isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

}
