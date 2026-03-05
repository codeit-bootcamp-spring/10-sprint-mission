package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto createMessage(CreateMessageRequestDTO dto, List<CreateBinaryContentPayloadDTO> attachments) {
        findUserOrThrow(dto.authorId());
        findChannelOrThrow(dto.channelId());
        List<UUID> attachment = new ArrayList<>();
        // 껍데기 생성
        Message message = MessageMapper.toEntity(dto, attachment);

        if (attachments != null && !attachments.isEmpty()) {
            for (var payload : attachments) {
                BinaryContent bc = BinaryContentMapper.toEntity(dto.authorId(), message.getId(), payload);
                binaryContentRepository.save(bc);
                attachment.add(bc.getId());
            }
        }
        // 영속화
        messageRepository.save(message);

        return MessageMapper.toResponse(message);
    }

    @Override
    public List<MessageDto> findAllByUserId(UUID userId) {
        findUserOrThrow(userId);
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getSentUserId().equals(userId))
                .toList();

        return MessageMapper.toResponseList(messages);
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        findChannelOrThrow(channelId);

        return MessageMapper.toResponseList(messageRepository.findByChannel_Id(channelId));
    }

    @Override
    public MessageDto findByMessageId(UUID messageId) {
        return MessageMapper.toResponse(findMessageOrThrow(messageId));
    }

    @Override
    public MessageDto updateMessage(UUID messageId, UpdateMessageRequestDTO dto) {
        Message message = findMessageOrThrow(messageId);

        if (dto.newContent() == null) {
            throw new IllegalArgumentException("content는 null값일 수 없습니다.");
        }

        message.updateContent(dto.newContent());
        messageRepository.save(message);

        return MessageMapper.toResponse(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        List<UUID> ids = findMessageOrThrow(messageId).getAttachmentIds();

        for (UUID id: ids) {
            binaryContentRepository.deleteById(id);
        }

        messageRepository.deleteById(messageId);
    }

    private Message findMessageOrThrow(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 null값일 수 없습니다.");

        return messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 메시지가 존재하지 않습니다."));
    }

    private Channel findChannelOrThrow(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        return channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다."));
    }

    private User findUserOrThrow(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null 값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 사용자가 존재하지 않습니다."));
    }
}
