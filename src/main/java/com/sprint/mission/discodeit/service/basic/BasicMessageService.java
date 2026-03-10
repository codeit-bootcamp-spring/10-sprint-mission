package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public MessageDto createMessage(CreateMessageRequestDTO dto, List<CreateBinaryContentPayloadDTO> attachments) {
        User user = findUserOrThrow(dto.authorId());
        Channel channel = findChannelOrThrow(dto.channelId());
        List<BinaryContent> attachmentEntities = new ArrayList<>();

        if (attachments != null && !attachments.isEmpty()) {
            for (CreateBinaryContentPayloadDTO payload : attachments) {
                BinaryContent bc = binaryContentMapper.toEntity(payload);
                attachmentEntities.add(bc);
            }
        }

        Message message = new Message(user, channel, dto.content(), attachmentEntities);

        user.getUserStatus().updateLastActiveAt(Instant.now());
        // id를 만들기 위해 저장
        Message savedMessage = messageRepository.saveAndFlush(message);
        // storage에 반영
        if (attachments != null && !attachments.isEmpty()) {
            List<BinaryContent> savedAttachments = savedMessage.getAttachments();
            for (int i = 0; i < attachments.size(); i++) {
                binaryContentStorage.put(
                        savedAttachments.get(i).getId(),
                        attachments.get(i).bytes()
                );
            }
        }

        return messageMapper.toDto(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByUserId(UUID userId, Instant cursor, int size) {
        findUserOrThrow(userId);
        Pageable pageable = PageRequest.of(0, size);
        Slice<Message> messages;

        if (cursor == null) {
            messages = messageRepository.findByAuthor_IdOrderByCreatedAtDesc(userId, pageable);
        } else {
            messages = messageRepository.findByAuthorIdWithCursor(userId, cursor, pageable);
        }
        
        List<MessageDto> contents = messageMapper.toDtoList(messages.getContent());

        Object nextCursor = null;
        if (!messages.getContent().isEmpty()) {
            Message lastMessage = messages.getContent().get(messages.getContent().size() - 1);
            nextCursor = lastMessage.getCreatedAt();
        }

        return pageResponseMapper.fromSlice(messages, contents, nextCursor);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
        findChannelOrThrow(channelId);
        // 요구사항 -> 50개씩 정렬 + cursor에선 항상 pageNumber=0
        Pageable pageable = PageRequest.of(0, size);
        Slice<Message> messages;

        if (cursor == null) {
            // cursor가 없는 경우 첫페이지
            messages = messageRepository.findByChannel_IdOrderByCreatedAtDesc(channelId, pageable);
        } else {
            // cursor가 있는 경우 cursor 반영
            messages = messageRepository.findByChannelIdWithCursor(channelId, cursor, pageable);
        }

        List<MessageDto> contents = messageMapper.toDtoList(messages.getContent());

        Object nextCursor = null;
        if (!messages.getContent().isEmpty()) {
            // 마지막 메시지 계산
            Message lastMessage = messages.getContent().get(messages.getContent().size() - 1);
            nextCursor = lastMessage.getCreatedAt();
        }

        return pageResponseMapper.fromSlice(messages, contents, nextCursor);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto findByMessageId(UUID messageId) {
        return messageMapper.toDto(findMessageOrThrow(messageId));
    }

    @Override
    public MessageDto updateMessage(UUID messageId, UpdateMessageRequestDTO dto) {
        Message message = findMessageOrThrow(messageId);

        if (dto.newContent() == null) {
            throw new IllegalArgumentException("content는 null값일 수 없습니다.");
        }

        message.updateContent(dto.newContent());

        return messageMapper.toDto(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        findMessageOrThrow(messageId).getAttachments();

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
