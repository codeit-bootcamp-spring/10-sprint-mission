package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageSentEvent;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.InvalidMessageContentException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public MessageDto create(CreateMessageRequestDto request, List<BinaryContentDto> attachments) {
        log.debug("메시지 생성 요청: channelId={}, authorId={}", request.channelId(), request.authorId());

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new ChannelNotFoundException(request.channelId()));
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new UserNotFoundException(request.authorId()));

        Message message = new Message(request.content(), channel, author);

        if ((request.content() == null || request.content().trim().isEmpty())
                && (attachments == null || attachments.isEmpty())) {
            log.warn("메시지 생성 실패 - 텍스트 및 첨부파일 누락: authorId={}", request.authorId());
            throw new InvalidMessageContentException(request.authorId());
        }

        if (attachments != null && !attachments.isEmpty()) {
            for (BinaryContentDto dto : attachments) {
                BinaryContentDto normalized = normalizeAttachment(dto);
                BinaryContent content = new BinaryContent(
                        normalized.fileName(),
                        normalized.contentType(),
                        normalized.size()
                );

                content = binaryContentRepository.save(content);
                if(normalized.bytes() != null){
                    binaryContentStorage.put(content.getId(), normalized.bytes());
                }

                message.addAttachment(content);
            }
        }
        // 메시지 생성 및 저장
        messageRepository.save(message);
        // 메시지 생성 시 그 유저는 활동중임을 나타냄
        eventPublisher.publishEvent(new MessageSentEvent(request.authorId()));

        log.info("메시지 생성 성공: messageId={}", message.getId());

        return messageMapper.toDto(message);
    }

    @Override
    public MessageDto find(UUID messageId) {
        Message message = getMessageEntity(messageId);
        return messageMapper.toDto(message);
    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        if (!channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(channelId);
        }
        Slice<Message> messageSlice;
        if (cursor == null) {
            messageSlice = messageRepository.findByChannelId(channelId, pageable);
        } else {
            messageSlice = messageRepository.findByChannelIdAndCreatedAtLessThan(channelId, cursor, pageable);
        }

        Slice<MessageDto> messageDtoSlice = messageSlice.map(messageMapper::toDto);

        Instant nextCursor = null;
        if (messageSlice.hasNext() && !messageSlice.getContent().isEmpty()) {
            List<Message> content = messageSlice.getContent();
            nextCursor = content.get(content.size() - 1).getCreatedAt();
        }

        return pageResponseMapper.fromSlice(messageDtoSlice, nextCursor);
    }

    @Override
    @Transactional
    public MessageDto update(UUID messageId, UpdateMessageRequestDto request) {
        log.debug("메시지 수정 요청: messageId={}", messageId);

        Message message = getMessageEntity(messageId);
        message.update(request.newContent());

        log.info("메시지 수정 완료: messageId={}", message.getId());
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        log.warn("메시지 삭제 요청: messageId={}", messageId);
        Message message = getMessageEntity(messageId);

        List<BinaryContent> attachmentsToDelete = new ArrayList<>(message.getAttachments());
        detachAttachments(message);
        deleteAttachedFiles(attachmentsToDelete);

        log.info("메시지 삭제 완료: messageId={}", messageId);
        messageRepository.delete(message);
    }

    //  ------ 내부 메서드 -------

    // 메시지 엔티티 조회
    private Message getMessageEntity(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
    }

    private BinaryContentDto normalizeAttachment(BinaryContentDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Attachment must not be null");
        }
        if (dto.size() == null || dto.size() <= 0) {
            throw new IllegalArgumentException("Attachment is empty");
        }

        String fileName = dto.fileName();
        if (fileName == null || fileName.isBlank()) {
            fileName = "unknown";
        }

        String contentType = dto.contentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        return new BinaryContentDto(fileName, contentType, dto.size(), dto.bytes());
    }


    // 메시지에 포함된 첨부파일 삭제
    private void detachAttachments(Message message) {
        if (message.getAttachments() == null || message.getAttachments().isEmpty()) {
            return;
        }
        message.getAttachments().clear();
        messageRepository.save(message);
        messageRepository.flush();
    }

    private void deleteAttachedFiles(List<BinaryContent> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        for (BinaryContent file : attachments) {
            binaryContentRepository.deleteById(file.getId());
        }
    }

}
