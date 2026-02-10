package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.common.BinaryContentParam;
import com.sprint.mission.discodeit.dto.message.MessageRequestCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestUpdateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponseDto create(MessageRequestCreateDto request) {
        Validators.validateCreateMessageRequest(request);
        User user = userRepository.findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        Validators.validationMessage(request.content());

        List<BinaryContentParam> attachments =
                request.attachments() == null ? List.of() : request.attachments();

        validateAttachments(attachments);

        List<UUID> attachmentIds = new ArrayList<>(attachments.size());

        for(BinaryContentParam a : attachments) {
            BinaryContent saved = binaryContentRepository.save(new BinaryContent(a.bytes(), a.contentType()));
            attachmentIds.add(saved.getId());
        }

        Message message = new Message(request.content(), request.authorId(), request.channelId(), attachmentIds);
        channel.getMessageIds().add(message.getId());
        user.getMessageIds().add(message.getId());
        return toDto(messageRepository.save(message));
    }

    @Override
    public MessageResponseDto find(UUID id) {
        Message message = validateExistenceMessage(id);
        return toDto(message);
    }

    @Override
    public List<MessageResponseDto> findByChannelId(UUID id) {
        return messageRepository.findAll().stream()
                .filter(m -> id.equals(m.getChannelId()))
                .map(BasicMessageService::toDto)
                .toList();
    }

    @Override
    public MessageResponseDto update(MessageRequestUpdateDto request) {
        Message message = validateExistenceMessage(request.id());
        Optional.ofNullable(request.content())
                .ifPresent(cont -> {Validators.requireNotBlank(cont, "content");
                    message.updateContent(cont);
                });

        return toDto(messageRepository.save(message));
    }

    public void delete(UUID messageId) {
        Message message = validateExistenceMessage(messageId);
        UUID channelId = message.getChannelId();
        UUID authorId = message.getAuthorId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }

        channel.getMessageIds().remove(messageId);
        user.getMessageIds().remove(messageId);
        messageRepository.deleteById(messageId);
    }

    public List<MessageResponseDto> readMessagesByUser(UUID userId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getAuthorId().equals(userId))
                .map(BasicMessageService::toDto)
                .toList();
    }

    private Message validateExistenceMessage(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지 id는 존재하지 않습니다."));
    }

    private void validateAttachments(List<BinaryContentParam> attachments) {
        for (BinaryContentParam a : attachments) {
            if (a == null) {
                throw new IllegalArgumentException("attachments에 null 요소가 포함될 수 없습니다.");
            }
            if (a.bytes() == null || a.bytes().length == 0) {
                throw new IllegalArgumentException("첨부파일 데이터가 비어있습니다.");
            }
            if (a.contentType() == null || a.contentType().isBlank()) {
                throw new IllegalArgumentException("contentType은 필수입니다.");
            }
        }
    }
    public static MessageResponseDto toDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds(),
                message.getCreatedAt()
        );
    }



}
