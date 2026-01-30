package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Autowired(required = false)
    private BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto.Response create(MessageDto.CreateRequest request) {
        if (!userRepository.existsById(request.authorId())) throw new IllegalArgumentException("존재하지 않은 사용자 입니다.");
        if (!channelRepository.existsById(request.channelId())) throw new IllegalArgumentException("존재하지 않는 채널입니다.");

        List<UUID> attachmentIds = new ArrayList<>();
        if (request.attachments() != null && binaryContentRepository != null) {
            request.attachments().forEach(att -> {
                BinaryContent content = new BinaryContent(
                        UUID.randomUUID(),
                        att.fileName(),
                        att.data(),
                        Instant.now()
                );
                binaryContentRepository.save(content);
                attachmentIds.add(content.getId());
            });
        }

        Message message = new Message(
                request.authorId(),
                request.channelId(),
                request.content(),
                attachmentIds
        );
        return convertToResponse(messageRepository.save(message));
    }

    @Override
    public MessageDto.Response findById(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 입니다. ID: " + id));
        return convertToResponse(message);
    }

    @Override
    public List<MessageDto.Response> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public MessageDto.Response update(MessageDto.UpdateRequest request) {
        Message message = messageRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 입니다."));

        message.update(request.content());
        return convertToResponse(messageRepository.save(message));
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 입니다."));

        if (binaryContentRepository != null && message.getAttachmentIds() != null) {
            message.getAttachmentIds().forEach(binaryContentRepository::delete);
        }
        messageRepository.deleteById(messageId);
    }

    // [헬퍼 메서드] 요구사항에 맞는 Response DTO 변환
    private MessageDto.Response convertToResponse(Message message) {
        return new MessageDto.Response(
                message.getId(),
                message.getContent(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getAttachmentIds()
        );
    }
}
