package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageMapper;
    private final MessageMapper messageMapper;

    @Override
    public MessageDto createMessage(MessageDto.MessageCreateRequest messageReq,
                                    List<MultipartFile> attachments) throws IOException {
        Channel channel = getChannelOrThrow(messageReq.channelId());
        User author = getUserOrThrow(messageReq.authorId());
        Message msg = new Message(channel, author, messageReq.content());

        if (attachments != null) {
            for (MultipartFile attachment: attachments) {
                BinaryContent content = new BinaryContent(
                        attachment.getOriginalFilename(), attachment.getSize(), attachment.getContentType());
                binaryContentRepository.save(content);
                binaryContentStorage.put(content.getId(), attachment.getBytes());
                msg.addAttachment(content);
            }
        }

        messageRepository.save(msg);
        return toResponse(msg);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Object cursor, Pageable pageable) {
        Pageable sizePlusOne = PageRequest.of(0, pageable.getPageSize() + 1, pageable.getSort());

        List<Message> messages = cursor == null
                ? messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, sizePlusOne)
                : messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(channelId, (Instant) cursor, sizePlusOne);
        boolean hasNext = messages.size() > pageable.getPageSize();
        Instant nextCursor = null;
        if (hasNext) {
            nextCursor = messages.get(pageable.getPageSize()).getCreatedAt();
            messages = messages.subList(0, pageable.getPageSize());
        }

        List<MessageDto> messagesDto = messages.stream().map(messageMapper::toDto).toList();
        return pageMapper.fromData(messagesDto, nextCursor, hasNext);
    }

    @Override
    public MessageDto updateMessage(UUID uuid, MessageDto.MessageUpdateRequest messageReq) {
        Message msg = messageRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.MESSAGE_NOT_FOUND));

        Optional.ofNullable(messageReq.newContent()).ifPresent(msg::updateMessage);
        messageRepository.save(msg);
        return toResponse(msg);
    }

    @Override
    public void deleteMessage(UUID uuid) throws IOException {
        Message msg = messageRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.MESSAGE_NOT_FOUND));
        for (var attachment : msg.getAttachments()) {
            binaryContentStorage.delete(attachment.getId());
        }
        messageRepository.deleteById(uuid);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    private MessageDto toResponse(Message msg) {
        return messageMapper.toDto(msg);
    }
}
