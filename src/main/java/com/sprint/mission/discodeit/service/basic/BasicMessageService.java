package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        validateAccess(request.authorId(), request.channelId());

        User author = getOrThrowUser(request.authorId());
        Channel channel = getOrThrowChannel(request.channelId());

        List<UUID> attachmentIds = request.attachmentIds();
        if (attachmentIds != null){
            attachmentIds.forEach(this::validateBinaryContentExists);
        }

        Message newMessage = new Message(request.content(), author,channel,attachmentIds);

        messageRepository.save(newMessage);
        return convertToResponse(newMessage);
    }


    @Override
    public MessageResponse findById(UUID id) {
        Message message = getOrThrowMessage(id);
        return convertToResponse(message);
    }

    // 특정 채널의 메시지 목록 조회
    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId, UUID userId) {
        validateAccess(userId, channelId);

        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public MessageResponse update(UUID id, MessageUpdateRequest request) {
        Message message = getOrThrowMessage(id);

        // 텍스트 내용 수정
        Optional.ofNullable(request.content()).ifPresent(message::updateContent);

        // 첨부파일 수정
        if (request.attachmentIds() != null){
            request.attachmentIds().forEach(this::validateBinaryContentExists);
            message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
            message.updateAttachmentIds(request.attachmentIds());
        }

        messageRepository.save(message);
        return convertToResponse(message);
    }

    @Override
    public void deleteById(UUID id) {
        Message message = getOrThrowMessage(id);

        // 첨부파일 삭제
        if (message.getAttachmentIds() != null) {
            message.getAttachmentIds().forEach(attachmentId -> {
                binaryContentRepository.deleteById(attachmentId);
            });
        }

        messageRepository.deleteById(id);
    }

    // 메시지 고정
    @Override
    public MessageResponse togglePin(UUID id){
        Message message = getOrThrowMessage(id);
        message.togglePin();

        messageRepository.save(message);
        return convertToResponse(message);
    }


    // 접근 권한 확인 (비공개 채널 여부 체크)
    private void validateAccess(UUID userId, UUID channelId) {
        Channel channel = getOrThrowChannel(channelId);
        if (channel.getVisibility() == ChannelVisibility.PRIVATE) {
            readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                    .orElseThrow(() -> new IllegalArgumentException("채널 접근 권한이 없습니다."));
        }
    }

    // 유저 검증
    private User getOrThrowUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
    }

    // 채널 검증
    private Channel getOrThrowChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다."));
    }

    // 메시지 검증
    private Message getOrThrowMessage(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다."));
    }

    // 첨부파일 검증
    private void validateBinaryContentExists(UUID id) {
        if (!binaryContentRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("해당 첨부파일을 찾을 수 없습니다.");
        }
    }

    // 엔티티 -> DTO 변환
    private MessageResponse convertToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getUser().getId(),
                message.getChannel().getId(),
                message.isPinned(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
