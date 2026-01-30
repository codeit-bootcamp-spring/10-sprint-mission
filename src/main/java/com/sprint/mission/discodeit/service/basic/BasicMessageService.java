package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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
        if (req == null) throw new IllegalArgumentException("request is null");
        if (req.content() == null || req.content().isBlank()) {
            throw new IllegalArgumentException("content is blank");
        }

        // 존재 검증
        channelRepository.findChannel(req.channelId());
        if (userRepository.findById(req.userId()).isEmpty()) throw new UserNotFoundException();

        // 첨부파일(선택) 검증
        List<UUID> attachmentIds = req.attachmentIds() == null ? List.of() : req.attachmentIds();
        if (!attachmentIds.isEmpty()) {
            // findAllByIdIn 이나 existsAllByIdIn 같은 메소드로 바꿔도 됨
            var found = binaryContentRepository.findAllByIdIn(attachmentIds);
            if (found.size() != attachmentIds.size()) {
                throw new IllegalArgumentException("attachmentIds contains non-existing id");
            }
        }

        // 메시지 생성/저장 (Repository/Entity는 너가 리팩토링할 시그니처 기준)
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
        channelRepository.findChannel(channelId);

        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest req) {
        if (req == null) throw new IllegalArgumentException("request is null");
        if (req.content() == null || req.content().isBlank()) {
            throw new IllegalArgumentException("content is blank");
        }

        Message message = messageRepository.findById(req.messageId())
                .orElseThrow(MessageNotFoundException::new);

        message.updateContent(req.content());
        Message saved = messageRepository.save(message);

        return toResponse(saved);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(MessageNotFoundException::new);

        List<UUID> attachmentIds = message.getAttachmentIds() == null
                ? Collections.emptyList()
                : message.getAttachmentIds();

        // 첨부파일 같이 삭제
        for (UUID attachmentId : attachmentIds) {
            binaryContentRepository.delete(attachmentId);
        }

        messageRepository.delete(messageId);
    }

    private MessageResponse toResponse(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getChannelId(),
                m.getUserId(),
                m.getContent(),
                m.getAttachmentIds() == null ? List.of() : m.getAttachmentIds(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}

