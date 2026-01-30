package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponseDto create(MessageCreateDto dto) {
        if (!channelRepository.existsById(dto.getChannelId())) {
            throw new NoSuchElementException("Channel not found");
        }
        if (!userRepository.existsById(dto.getSenderId())) {
            throw new NoSuchElementException("User not found");
        }

        Message message = new Message(dto.getSenderId(), dto.getChannelId(), dto.getContent());

        List<UUID> attachmentIds = new ArrayList<>();
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            for (BinaryContentDto fileDto : dto.getAttachments()) {
                BinaryContent content = new BinaryContent(fileDto.getBytes(), fileDto.getFileName(), fileDto.getContentType());
                content.setParentMessage(message.getId());
                binaryContentRepository.save(content);
                attachmentIds.add(content.getId());
            }
        }
        message.assignAttachments(attachmentIds);
        messageRepository.save(message);
        return convertToResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findallByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    public MessageResponseDto update(MessageUpdateDto dto) {
        Message message = messageRepository.findById(dto.getMessageId())
                .orElseThrow(() -> new NoSuchElementException("Message not found"));

        if (dto.getContent() != null && !dto.getContent().isBlank()) {
            message.updateContent(dto.getContent());
            messageRepository.save(message);
        }

        return convertToResponseDto(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));

        if (message.getAttachmentIds() != null) {
            for (UUID fileId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(fileId);
            }
        }

        messageRepository.deleteById(messageId);
    }

    private MessageResponseDto convertToResponseDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getSenderId(),
                message.getChannelId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getAttachmentIds()
        );
    }

    @Override
    public MessageResponseDto find(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));
        return convertToResponseDto(message);
    }
}