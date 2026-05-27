package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest messageCreateRequest,
            List<MultipartFile> attachments)
            throws IOException {

        log.info("메시지 생성 시작: authorId={}, channelId={}", messageCreateRequest.authorId(),
                messageCreateRequest.channelId());

        User author = userRepository.findById(messageCreateRequest.authorId())
                .orElseThrow(() -> {
                    log.warn("메시지 생성 실패 - 존재하지 않는 사용자: authorId={}",
                            messageCreateRequest.authorId());
                    return new UserNotFoundException(messageCreateRequest.authorId());
                });
        Channel channel = channelRepository.findById(messageCreateRequest.channelId())
                .orElseThrow(() -> {
                    log.warn("메시지 생성 실패 - 존재하지 않는 채널: channelId={}",
                            messageCreateRequest.channelId());
                    return new ChannelNotFoundException(messageCreateRequest.channelId());
                });

        List<BinaryContent> savedContents = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile file : attachments) {
                BinaryContent content = new BinaryContent(file.getContentType(), file.getSize(),
                        file.getOriginalFilename());

                binaryContentRepository.saveAndFlush(content);

                binaryContentStorage.put(content.getId(), file.getBytes());
                savedContents.add(content);
                log.debug("첨부파일 저장 완료: fileName={}", content.getFileName());
            }
        }

        Instant now = Instant.now();
        Message message = new Message(author, channel,
                messageCreateRequest.content(), savedContents);
        messageRepository.save(message);

        readStatusRepository.findByUserIdAndChannelId(messageCreateRequest.authorId(),
                        channel.getId())
                .ifPresent(rs -> rs.updateLastReadAt(now.plusMillis(10))); // 찰나의 차이로 본인 메시지 안 읽음 방지

        log.info("메시지 생성 완료: id={}, authorId={}, channelId={}", message.getId(), author.getId(),
                channel.getId());

        return messageMapper.toMessageDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto findById(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));

        return messageMapper.toMessageDto(message);
    }

    @Override
    @Transactional
    public PageResponse<MessageDto> findAllByChannelId(UUID userId, UUID channelId, UUID cursor,
            Pageable pageable) {

        readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .ifPresentOrElse(
                        rs -> rs.updateLastReadAt(Instant.now()),
                        () -> {
                            // 없으면(Public 채널 첫 방문 등) 새로 생성
                            User user = userRepository.findById(userId)
                                    .orElseThrow(
                                            () -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
                            Channel channel = channelRepository.findById(channelId)
                                    .orElseThrow(
                                            () -> new IllegalArgumentException("존재하지 않는 채널입니다."));
                            readStatusRepository.save(new ReadStatus(user, channel, Instant.now()));
                        }
                );

        Slice<Message> messageSlice = messageRepository.findAllByChannelId(channelId, pageable);
        return pageResponseMapper.fromSlice(messageSlice.map(messageMapper::toMessageDto));
    }

    @Override
    @Transactional
    public MessageDto update(UUID id, MessageUpdateRequest messageUpdateRequest) {
        log.info("메시지 수정 시작: id={}", id);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("메시지 수정 실패 - 존재하지 않는 메시지: id={}", id);
                    return new MessageNotFoundException(id);
                });

        message.updateContent(messageUpdateRequest.newContent());

        log.info("메시지 수정 완료: id={}", id);
        return messageMapper.toMessageDto(message);
    }

//  @Override
//  @Transactional
//  public PageResponse<MessageDto> searchMessage(UUID userId, UUID channelId, String keyword,
//      Pageable pageable) {
//    channelRepository.findById(channelId)
//        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다."));
//
//    Slice<Message> messageSlice = messageRepository.findByChannelIdAndContentContaining(channelId,
//        keyword, pageable);
//    return pageResponseMapper.fromSlice(messageSlice.map(messageMapper::toMessageDto));
//  }
//
//  @Override
//  @Transactional(readOnly = true)
//  public PageResponse<MessageDto> getUserMessages(UUID id, Pageable pageable) {
//    Slice<Message> messageSlice = messageRepository.findAllByUserId(id, pageable);
//    return pageResponseMapper.fromSlice(messageSlice.map(messageMapper::toMessageDto));
//  }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> getMessages(UUID channelId, Instant cursor, Pageable pageable) {
        if (!channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(channelId);
        }
        Slice<Message> messageSlice = (cursor == null)
                ? messageRepository.findAllByChannelId(channelId, pageable)
                : messageRepository.findAllByChannelIdBeforeCursor(channelId, cursor, pageable);

        List<MessageDto> content = messageSlice.getContent().stream()
                .filter(m -> cursor == null || m.getCreatedAt().isBefore(cursor))
                .map(messageMapper::toMessageDto)
                .toList();

        Instant nextCursor = messageSlice.hasNext() && !content.isEmpty()
                ? content.get(content.size() - 1).createdAt()
                : null;

        return new PageResponse<>(
                content,
                nextCursor,
                messageSlice.getSize(),
                messageSlice.hasNext(),
                null
        );
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("메시지 삭제 시작: id={}", id);
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("메시지 삭제 실패 - 존재하지 않는 메시지: id={}", id);
                    return new MessageNotFoundException(id);
                });

        log.info("메시지 삭제 완료: id={}", id);
        // 메시지 자체 삭제
        messageRepository.deleteById(id);
    }
}
