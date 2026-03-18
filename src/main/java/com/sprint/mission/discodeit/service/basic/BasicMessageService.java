package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private static final int DEFAULT_MESSAGE_PAGE_SIZE = 50;

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentService binaryContentService;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageDto createMessage(
            UUID requesterId,
            MessageCreateRequest request,
            List<BinaryContentRequest> attachments
    ) {
        User user = getUserOrThrow(requesterId);
        Channel channel = getChannelOrThrow(request.channelId());

        Message message = new Message(user, channel, request.content());

        List<BinaryContent> attachmentEntities = binaryContentService.createBinaryContents(
                attachments
        );
        message.updateAttachments(attachmentEntities);

        Message savedMessage = messageRepository.save(message);
        channel.updateLastMessageAt(savedMessage.getCreatedAt());

        return messageMapper.toDto(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllMessagesByChannelId(
            UUID channelId,
            UUID cursor,
            Pageable pageable
    ) {
        Pageable cursorPageable = buildCursorPageable(pageable);
        Slice<MessageDto> messageSlice = getMessageSlice(channelId, cursor, cursorPageable)
                .map(messageMapper::toDto);
        Object nextCursor = getNextCursor(messageSlice);

        return pageResponseMapper.fromSlice(messageSlice, nextCursor);
    }

    @Override
    @Transactional
    public MessageDto updateMessage(UUID messageId, MessageUpdateRequest request) {
        Message message = getMessageOrThrow(messageId);
        message.updateContent(request.newContent());

        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID messageId) {
        Message message = getMessageOrThrow(messageId);
        message.getAttachments().clear();
        message.assignAuthor(null);
        message.assignChannel(null);

        messageRepository.delete(message);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private Channel getChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHANNEL_NOT_FOUND,
                        "채널을 찾을 수 없습니다 channelId: " + id));
    }

    private Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND,
                        "메세지를 찾을 수 없습니다 messageId: " + messageId));
    }

    private Slice<Message> getMessageSlice(UUID channelId, UUID cursor, Pageable pageable) {
        if (cursor == null) {
            return messageRepository.findByChannel_IdOrderByCreatedAtDescIdDesc(
                    channelId,
                    pageable
            );
        }
        Message cursorMessage = getMessageOrThrow(cursor);
        validateCursorChannel(cursorMessage, channelId);

        return messageRepository.findNextPageByChannelIdAndCursor(
                channelId,
                cursorMessage.getCreatedAt(),
                cursorMessage.getId(),
                pageable
        );
    }

    private Object getNextCursor(Slice<MessageDto> messageSlice) {
        if (!messageSlice.hasNext() || messageSlice.getContent().isEmpty()) {
            return null;
        }

        MessageDto lastMessage = messageSlice.getContent()
                .get(messageSlice.getContent().size() - 1);
        return lastMessage.id();
    }

    private Pageable buildCursorPageable(Pageable pageable) {
        int size = pageable == null || pageable.getPageSize() <= 0
                ? DEFAULT_MESSAGE_PAGE_SIZE
                : pageable.getPageSize();

        return PageRequest.of(
                0,
                size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")
                )
        );
    }

    private void validateCursorChannel(Message cursorMessage, UUID channelId) {
        if (!cursorMessage.getChannel().getId().equals(channelId)) {
            throw new ApiException(
                    ErrorCode.MESSAGE_NOT_FOUND,
                    "커서 메시지가 채널에 속하지 않습니다. messageId: " + cursorMessage.getId()
                            + ", channelId: " + channelId
            );
        }
    }
}
