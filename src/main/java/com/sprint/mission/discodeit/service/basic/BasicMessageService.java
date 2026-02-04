package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotInputException;
import com.sprint.mission.discodeit.exception.StatusNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
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
            throw new MessageNotInputException();
        }

        // 존재 검증
        channelRepository.findChannel(req.channelId()); // 없으면 ChannelNotFoundException
        if (userRepository.findById(req.userId()) == null) {
            throw new UserNotFoundException();
        }

        // 첨부파일(선택) 정리 + 방어적 복사
        List<UUID> attachmentIds = (req.attachmentIds() == null) ? List.of() : List.copyOf(req.attachmentIds());

        // 첨부파일 존재 검증(중복 제거 후 비교)
        if (!attachmentIds.isEmpty()) {
            List<UUID> distinctIds = attachmentIds.stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            var found = binaryContentRepository.findAllByIdIn(distinctIds);
            if (found.size() != distinctIds.size()) {
                throw new StatusNotFoundException();
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

        channelRepository.findChannel(channelId); // 없으면 ChannelNotFoundException

        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest req) {
        requireNonNull(req, "request");
        requireNonNull(req.messageId(), "messageId");

        if (req.content() == null || req.content().isBlank()) {
            throw new MessageNotInputException();
        }

        Message message = messageRepository.findById(req.messageId())
                .orElseThrow(MessageNotFoundException::new);

        message.updateContent(req.content());
        Message saved = messageRepository.save(message);

        return toResponse(saved);
    }

    @Override
    public void delete(UUID messageId) {
        requireNonNull(messageId, "messageId");

        Message message = messageRepository.findById(messageId)
                .orElseThrow(MessageNotFoundException::new);

        List<UUID> attachmentIds = (message.getAttachmentIds() == null)
                ? List.of()
                : List.copyOf(message.getAttachmentIds());

        for (UUID attachmentId : attachmentIds) {
            if (attachmentId != null) {
                binaryContentRepository.delete(attachmentId); // 없으면(StatusNotFoundException 등) 구현체 기준
            }
        }

        messageRepository.delete(messageId);
    }

    private MessageResponse toResponse(Message m) {
        List<UUID> attachmentIds = (m.getAttachmentIds() == null) ? List.of() : List.copyOf(m.getAttachmentIds());

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
