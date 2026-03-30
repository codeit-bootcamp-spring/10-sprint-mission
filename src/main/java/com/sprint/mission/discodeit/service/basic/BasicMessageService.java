package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessagePatchDto;
import com.sprint.mission.discodeit.dto.MessagePostDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public MessageDto create(MessagePostDto messagePostDto, List<MultipartFile> attachments)
        throws RuntimeException {
        // 메시지를 생성 전, 유저가 해당 채널에 속해있는지 확인한다.
        Channel channel = channelRepository.findById(messagePostDto.channelId())
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND,
                    messagePostDto.channelId())
            );
        User user = userRepository.findById(messagePostDto.authorId())
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND,
                    messagePostDto.authorId())
            );

        // private channel은 참가자만 메시지를 보낼 수 있음.
        if (channel.getType() == ChannelType.PRIVATE && user.getReadStatusList().stream()
            .noneMatch(
                readStatus -> readStatus.getChannel().getId().equals(messagePostDto.channelId()))) {
            throw new BusinessLogicException(ExceptionCode.NOT_INVITED_IN_CHANNEL);
        }

        Message newMessage = new Message(
            messagePostDto.content(),
            channel,
            user
        );

        // 선택적으로 첨부파일 등록(첨부파일 영속화 + 레코드 추가 + 양방향 연결)
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile attachment : attachments) {
                try {
                    BinaryContent binaryContent = new BinaryContent(
                        attachment.getOriginalFilename(),
                        (int) attachment.getSize(),
                        attachment.getContentType()
                    );

                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), attachment.getBytes());

                    newMessage.addAttachment(binaryContent); // 메시지에 파일 정보 업데이트
                } catch (IOException e) {
                    throw new BusinessLogicException(ExceptionCode.ATTACHMENT_SAVE_EXCEPTION);
                }
            }
        }

        messageRepository.save(newMessage);

        // channel과 user의 messageList에 현재 message를 add
        channel.addMessage(newMessage);
        user.addMessage(newMessage);

        return messageMapper.toDto(newMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto findById(UUID id) {
        return messageMapper.toDto(messageRepository.findById(id)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND, id)
            )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> findByUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, userId)
            );

        return user.getMessageList().stream()
            .map(messageMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findByChannelId(UUID channelId, Instant cursor,
        Pageable pageable) {
        Slice<Message> messageSlice;

        if (cursor == null) {
            messageSlice = messageRepository
                .findByChannelIdOrderByCreatedAtDesc(channelId, pageable);
        } else {
            messageSlice = messageRepository
                .findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(channelId, cursor, pageable
                );
        }

        Slice<MessageDto> messageDtoSlice = messageSlice.map(messageMapper::toDto);
        Instant nextCursor = null;

        if (messageSlice.hasNext() && !messageSlice.getContent().isEmpty()) {
            nextCursor = messageSlice.getContent()
                .get(messageSlice.getContent().size() - 1)
                .getCreatedAt();
        }

        return pageResponseMapper.fromSlice(messageDtoSlice, nextCursor);
    }

    @Override
    public MessageDto updateById(UUID messageId, MessagePatchDto messagePatchDto) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND, messageId)
            );

        message.updateContent(messagePatchDto.newContent());

        return messageMapper.toDto(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND, messageId);
        }

        messageRepository.deleteById(messageId);
    }
}
