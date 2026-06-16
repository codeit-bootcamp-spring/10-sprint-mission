package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.*;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageAccessDeniedException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 메시지 관련 비즈니스 로직을 처리하는 기본 서비스 클래스입니다.
 * 메시지 생성, 조회, 수정, 삭제 및 채널별 메시지 페이징 조회를 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 새로운 메시지를 생성합니다.
     * 비공개 채널의 경우 참여 여부를 확인하며, 첨부 파일 유효성을 검사합니다.
     *
     * @param request 메시지 생성 요청 정보
     * @param attachmentIds 첨부 파일 ID 목록
     * @return 생성된 메시지 상세 정보
     */
    @Override
    @Transactional
    public MessageDto.Response create(MessageDto.CreateRequest request, List<UUID> attachmentIds) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> ChannelNotFoundException.withId(request.channelId()));

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> UserNotFoundException.withId(request.authorId()));

        validateChannelAccess(author.getId(), channel);

        List<BinaryContent> attachments = validateAndGetAttachments(attachmentIds);

        Message message = new Message(request.content(), channel, author, attachments);
        Message savedMessage = messageRepository.save(message);

        // 채널의 마지막 활동 시간 갱신
        channel.updateLastMessageAt(savedMessage.getCreatedAt());

        log.info("[Message] 메시지 생성 완료: ID={}, Author={}, Channel={}", 
                savedMessage.getId(), author.getUsername(), channel.getId());

        // 알림 및 부가 처리를 위한 이벤트 발행
        eventPublisher.publishEvent(new MessageEvents.Created(savedMessage.getId()));
        
        // 캐시 무효화 (채널 목록의 lastMessageAt 동기화)
        List<UUID> participantIds = (channel.getType() == ChannelType.PRIVATE)
                ? readStatusRepository.findParticipantIdsByChannelId(channel.getId())
                : List.of();
        eventPublisher.publishEvent(new ChannelEvents.Updated(channel.getId(), channel.getType(), participantIds));
        
        return messageMapper.toResponse(savedMessage);
    }

    /**
     * 메시지를 ID로 조회합니다.
     */
    @Override
    public MessageDto.Response find(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> MessageNotFoundException.withId(messageId));
    }

    /**
     * 특정 채널의 메시지 목록을 커서 기반 페이징으로 조회합니다.
     *
     * @param channelId 채널 ID
     * @param cursor 커서 (마지막 메시지의 생성 시간)
     * @param pageable 페이징 정보
     * @return 메시지 페이징 응답
     */
    @Override
    public PageResponse<MessageDto.Response> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        if (!channelRepository.existsById(channelId)) {
            throw ChannelNotFoundException.withId(channelId);
        }

        log.debug("[Message] 채널 메시지 목록 조회: ChannelId={}, Cursor={}", channelId, cursor);

        Pageable cursorPageable = createCursorPageable(pageable);
        Slice<Message> messageSlice = fetchMessages(channelId, cursor, cursorPageable);
        
        Slice<MessageDto.Response> responseSlice = messageSlice.map(messageMapper::toResponse);
        String nextCursor = extractNextCursor(responseSlice);

        return pageResponseMapper.fromSlice(responseSlice, nextCursor);
    }

    /**
     * 메시지 내용을 수정합니다. (작성자 본인만 가능)
     */
    @Override
    @Transactional
    @PreAuthorize("@messageAuth.isAuthor(#messageId, principal.userDto.id)")
    public MessageDto.Response update(UUID messageId, MessageDto.UpdateRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> MessageNotFoundException.withId(messageId));
        
        message.update(request.newContent());
        log.info("[Message] 메시지 수정 완료: ID={}", messageId);

        return messageMapper.toResponse(message);
    }

    /**
     * 메시지를 삭제합니다. (작성자 본인만 가능)
     * 삭제 후 채널의 lastMessageAt을 재계산합니다.
     */
    @Override
    @Transactional
    @PreAuthorize("@messageAuth.isAuthor(#messageId, principal.userDto.id)")
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> MessageNotFoundException.withId(messageId));

        Channel channel = message.getChannel();
        messageRepository.delete(message);
        messageRepository.flush(); // 즉시 삭제 반영하여 lastMessageAt 재조회 시 제외

        // 삭제된 메시지가 마지막 메시지였을 경우를 대비해 최신 메시지 시간 재조회
        Instant latestMessageAt = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channel.getId())
                .map(Message::getCreatedAt)
                .orElse(null);

        channel.updateLastMessageAt(latestMessageAt);
        log.info("[Message] 메시지 삭제 완료: ID={}", messageId);

        // 캐시 무효화 (채널 목록의 lastMessageAt 동기화)
        List<UUID> participantIds = (channel.getType() == ChannelType.PRIVATE)
                ? readStatusRepository.findParticipantIdsByChannelId(channel.getId())
                : List.of();
        eventPublisher.publishEvent(new ChannelEvents.Updated(channel.getId(), channel.getType(), participantIds));
    }

    // --- Private Helpers ---

    private void validateChannelAccess(UUID userId, Channel channel) {
        if (channel.getType() == ChannelType.PRIVATE) {
            if (!readStatusRepository.existsByUserIdAndChannelId(userId, channel.getId())) {
                throw MessageAccessDeniedException.privateChannel(userId, channel.getId());
            }
        }
    }

    private List<BinaryContent> validateAndGetAttachments(List<UUID> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<UUID> uniqueIds = new HashSet<>(attachmentIds);
        List<BinaryContent> attachments = binaryContentRepository.findAllById(uniqueIds);

        if (attachments.size() != uniqueIds.size()) {
            Set<UUID> foundIds = attachments.stream().map(BinaryContent::getId).collect(Collectors.toSet());
            List<UUID> missingIds = uniqueIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw BinaryContentNotFoundException.withIds(uniqueIds.size(), missingIds);
        }
        return attachments;
    }

    private Pageable createCursorPageable(Pageable pageable) {
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(0, pageable.getPageSize(), sort);
    }

    private Slice<Message> fetchMessages(UUID channelId, Instant cursor, Pageable pageable) {
        return (cursor == null)
                ? messageRepository.findLatestByChannelId(channelId, pageable)
                : messageRepository.findAllUseCursorByChannelId(channelId, cursor, pageable);
    }

    private String extractNextCursor(Slice<MessageDto.Response> slice) {
        if (slice.hasNext() && !slice.getContent().isEmpty()) {
            return slice.getContent().get(slice.getContent().size() - 1).createdAt().toString();
        }
        return null;
    }
}
