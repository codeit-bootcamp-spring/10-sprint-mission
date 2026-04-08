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
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.global.InvalidInputException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

        log.info("[MESSAGE_CREATE_SUCCESS] 메시지 생성 성공: messageId={}", message.getId());
        return messageMapper.toDto(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByUserId(UUID userId, Instant cursor, Pageable pageable) {
        findUserOrThrow(userId);
        Pageable pageRequest = PageRequest.of(0, pageable.getPageSize());
        Slice<Message> messages;

        if (cursor == null) {
            messages = messageRepository.findByAuthor_IdOrderByCreatedAtDesc(userId, pageRequest);
        } else {
            messages = messageRepository.findByAuthorIdWithCursor(userId, cursor, pageRequest);
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
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        findChannelOrThrow(channelId);
        // 요구사항 -> 50개씩 정렬 + cursor에선 항상 pageNumber=0
        Pageable pageRequest = PageRequest.of(0, pageable.getPageSize());
        Slice<Message> messages;

        if (cursor == null) {
            // cursor가 없는 경우 첫페이지
            messages = messageRepository.findByChannel_IdOrderByCreatedAtDesc(channelId, pageRequest);
        } else {
            // cursor가 있는 경우 cursor 반영
            messages = messageRepository.findByChannelIdWithCursor(channelId, cursor, pageRequest);
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
            log.warn("[MESSAGE_UPDATE_FAIL_BY_CONTENT] 내용이 비어있어서 메시지 수정 실패: messageId={}", messageId);
            throw new InvalidInputException(
                    ErrorCode.MESSAGE_CONTENT_IS_BLANK, Map.of("messageId", messageId)
            );
        }

        message.updateContent(dto.newContent());

        log.info("[MESSAGE_UPDATE_SUCCESS] 메시지 수정 성공: messageId={}", messageId);
        return messageMapper.toDto(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        findMessageOrThrow(messageId).getAttachments();

        log.info("[MESSAGE_DELETE_SUCCESS] 메시지 삭제 성공: messageId={}", messageId);
        messageRepository.deleteById(messageId);
    }

    private Message findMessageOrThrow(UUID messageId) {
        if (messageId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("messageId", "messageId is null")
            );
        }

        return messageRepository.findById(messageId)
                .orElseThrow(() ->
                {
                    log.warn("[MESSAGE_NOT_FOUND] 메시지가 존재하지 않음: messageId={}", messageId);
                    return new MessageNotFoundException(messageId);
                });
    }

    private Channel findChannelOrThrow(UUID channelId) {
        if (channelId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("channelId", "channelId is null")
            );
        }

        return channelRepository.findById(channelId)
                .orElseThrow(() ->
                {
                    log.warn("[CHANNEL_NOT_FOUND] 채널이 존재하지 않음: channelId={}", channelId);
                    return new ChannelNotFoundException(channelId);
                });
    }

    private User findUserOrThrow(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("userId", "userId is null")
            );
        }

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(userId));
    }
}
