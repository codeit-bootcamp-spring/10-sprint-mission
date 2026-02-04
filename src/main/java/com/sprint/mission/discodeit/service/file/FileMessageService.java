package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.status.BinaryContentRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileMessageService implements MessageService {
    // ✅ Repository만 주입 (Service 간 의존성 제거)
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    public FileMessageService(MessageRepository messageRepository,
                              ChannelRepository channelRepository,
                              UserRepository userRepository,
                              BinaryContentRepository binaryContentRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public MessageResponse create(CreateMessageRequest request) {
        // 1. 채널 존재 확인
        channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + request.getChannelId()));

        // 2. 작성자 존재 확인
        var author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getAuthorId()));

        // 3. 첨부파일 존재 확인
        List<UUID> validAttachmentIds = new ArrayList<>();
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            for (UUID attachmentId : request.getAttachmentIds()) {
                if (binaryContentRepository.existsById(attachmentId)) {
                    validAttachmentIds.add(attachmentId);
                } else {
                    throw new IllegalArgumentException("Attachment not found: " + attachmentId);
                }
            }
        }

        // 4. Message 생성 및 저장
        Message message = new Message(
                request.getContent(),
                request.getChannelId(),
                request.getAuthorId(),
                validAttachmentIds
        );
        messageRepository.save(message);  // ✅ FileMessageRepository가 개별 파일로 저장

        // 5. MessageResponse 반환
        UserResponse authorResponse = UserResponse.from(author);
        return MessageResponse.from(message, authorResponse, validAttachmentIds);
    }

    @Override
    public MessageResponse find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        var author = userRepository.findById(message.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + message.getAuthorId()));

        UserResponse authorResponse = UserResponse.from(author);
        return MessageResponse.from(message, authorResponse, message.getAttachmentIds());
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        // 채널 존재 확인
        channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));

        // 해당 채널의 메시지만 필터링
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(message -> {
                    var author = userRepository.findById(message.getAuthorId())
                            .orElseThrow(() -> new IllegalArgumentException("User not found: " + message.getAuthorId()));
                    UserResponse authorResponse = UserResponse.from(author);
                    return MessageResponse.from(message, authorResponse, message.getAttachmentIds());
                })
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse update(UpdateMessageRequest request) {
        Message message = messageRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + request.getId()));

        // 내용 수정
        message.update(request.getContent());
        messageRepository.save(message);  // ✅ 수정된 내용 저장

        // MessageResponse 반환
        var author = userRepository.findById(message.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + message.getAuthorId()));
        UserResponse authorResponse = UserResponse.from(author);
        return MessageResponse.from(message, authorResponse, message.getAttachmentIds());
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        // 1. 연관된 첨부파일(BinaryContent) 삭제
        if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }

        // 2. 메시지 삭제
        messageRepository.deleteById(messageId);  // ✅ 개별 파일 삭제
    }
}
