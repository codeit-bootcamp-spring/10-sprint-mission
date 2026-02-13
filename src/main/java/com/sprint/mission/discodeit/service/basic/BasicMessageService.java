package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(MessageCreateRequest req) {
        requireNonNull(req, "request");
        requireNonNull(req.channelId(), "channelId");
        requireNonNull(req.userId(), "userId");

        if (req.content() == null || req.content().isBlank()) {
            throw new BusinessLogicException(ErrorCode.MESSAGE_EMPTY);
        }

        // 존재 검증
        findChannelOrThrow(req.channelId());

        if (userRepository.findById(req.userId()) == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        List<UUID> attachmentIds =
                (req.attachmentIds() == null) ? List.of() : List.copyOf(req.attachmentIds());

        if (!attachmentIds.isEmpty()) {
            List<UUID> distinctIds = attachmentIds.stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            var found = binaryContentRepository.findAllByIdIn(distinctIds);
            if (found.size() != distinctIds.size()) {
                throw new BusinessLogicException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
            }
        }

        Message saved = messageRepository.save(
                new Message(
                        req.channelId(),
                        req.userId(),
                        req.content(),
                        attachmentIds
                )
        );

        return toResponse(saved);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        requireNonNull(channelId, "channelId");

        findChannelOrThrow(channelId);

        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest req) {
        requireNonNull(req, "request");
        requireNonNull(req.messageId(), "messageId");

        if (req.content() == null || req.content().isBlank()) {
            throw new BusinessLogicException(ErrorCode.MESSAGE_EMPTY);
        }

        Message message = messageRepository.findById(req.messageId())
                .orElseThrow(() ->
                        new BusinessLogicException(ErrorCode.MESSAGE_NOT_FOUND)
                );

        message.updateContent(req.content());
        Message saved = messageRepository.save(message);

        return toResponse(saved);
    }

    @Override
    public void delete(UUID messageId) {
        requireNonNull(messageId, "messageId");

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new BusinessLogicException(ErrorCode.MESSAGE_NOT_FOUND)
                );

        List<UUID> attachmentIds =
                (message.getAttachmentIds() == null) ? List.of() : List.copyOf(message.getAttachmentIds());

        for (UUID attachmentId : attachmentIds) {
            if (attachmentId != null) {
                binaryContentRepository.delete(attachmentId);
            }
        }

        messageRepository.delete(messageId);
    }

    private void findChannelOrThrow(UUID channelId) {
        if (channelRepository.findChannel(channelId) == null) {
            throw new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND);
        }
    }

    private MessageResponse toResponse(Message m) {
        List<UUID> attachmentIds =
                (m.getAttachmentIds() == null) ? List.of() : List.copyOf(m.getAttachmentIds());

        return new MessageResponse(
                m.getId(),
                m.getChannelId(),
                m.getUserId(),
                m.getContent(),
                attachmentIds,
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}
