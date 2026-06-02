package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.entity.*;
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
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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


    @Override
    @Transactional
    public MessageDto.Response create(MessageDto.CreateRequest request, List<UUID> attachmentIds) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> ChannelNotFoundException.withId(request.channelId()));

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> UserNotFoundException.withId(request.authorId()));

        if (channel.getType() == ChannelType.PRIVATE) {
            if (!readStatusRepository.existsByUserIdAndChannelId(request.authorId(), channel.getId())){
                throw MessageAccessDeniedException.privateChannel(request.authorId(), channel.getId());
            }
        }

        List<BinaryContent> attachments = new ArrayList<>();

        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            Set<UUID> uniqueAttachmentIds = new HashSet<>(attachmentIds);
            attachments = binaryContentRepository.findAllById(uniqueAttachmentIds);

            if (attachments.size() != uniqueAttachmentIds.size()) {
                Set<UUID> foundIds = attachments.stream()
                        .map(BinaryContent::getId)
                        .collect(Collectors.toSet());

                List<UUID> missingIds = uniqueAttachmentIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .toList();

                throw BinaryContentNotFoundException.withIds(uniqueAttachmentIds.size(), missingIds);
            }
        }

        Message message = new Message(request.content(), channel, author, attachments);
        Message savedMessage = messageRepository.save(message);

        Instant lastMessageAt = savedMessage.getCreatedAt() == null ? Instant.now() : savedMessage.getCreatedAt();
        channel.updateLastMessageAt(lastMessageAt);

        log.info("[Message Created] ID: {}, Author: {}, Channel: {}, Type: {}, Attachments: {}",
                savedMessage.getId(), author.getUsername(), channel.getId(), channel.getType(), attachments.size());

        return messageMapper.toResponse(savedMessage);
    }

    @Override
    public MessageDto.Response find(UUID messageId) {
        MessageDto.Response response = messageRepository.findById(messageId)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> MessageNotFoundException.withId(messageId));

        log.debug("[Message Found] ID: {}, Channel: {}, Author: {}",
                messageId, response.channelId(), response.author().username());

        return response;

    }

    @Override
    public PageResponse<MessageDto.Response> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {

        if(!channelRepository.existsById(channelId)) {
            throw ChannelNotFoundException.withId(channelId);
        }

        log.debug("[Messages Fetched] Channel: {}, Cursor: {}, PageSize: {}",
                channelId, cursor, pageable.getPageSize());

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable cursorPageable = PageRequest.of(0, pageable.getPageSize(), sort);

        Slice<Message> messageSlice;
        if (cursor == null) {
            messageSlice = messageRepository.findLatestByChannelId(channelId, cursorPageable);
        } else {
            messageSlice = messageRepository.findAllUseCursorByChannelId(channelId, cursor, cursorPageable);
        }

        Slice<MessageDto.Response> responseSlice = messageSlice.map(messageMapper::toResponse);

        String nextCursor = null;
        if (responseSlice.hasNext() && !responseSlice.getContent().isEmpty()) {
            MessageDto.Response lastMessage = responseSlice.getContent()
                    .get(responseSlice.getContent().size() - 1);
            nextCursor = lastMessage.createdAt().toString();
        }

        // messageSlice를 DTO로 변환
        return pageResponseMapper.fromSlice(responseSlice, nextCursor);
    }

    @PreAuthorize("@messageAuth.isAuthor(#messageId, principal.userDto.id)")
    @Override
    @Transactional
    public MessageDto.Response update(UUID messageId, MessageDto.UpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> MessageNotFoundException.withId(messageId));
        message.update(newContent);

        log.info("[Message Updated] ID: {}, Channel: {}, Author: {}",
                messageId, message.getChannel().getId(), message.getAuthor().getUsername());

        return messageMapper.toResponse(message);
    }

    @PreAuthorize("@messageAuth.isAuthor(#messageId, principal.userDto.id)")
    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> MessageNotFoundException.withId(messageId));

        Channel channel = message.getChannel();

        messageRepository.delete(message);
        messageRepository.flush();

        Instant latestMessageAt = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channel.getId())
                .map(Message::getCreatedAt)
                .orElse(null);

        channel.updateLastMessageAt(latestMessageAt);

        log.info("[Message Deleted] ID: {}, Channel: {}", messageId, channel.getId());
    }
}
