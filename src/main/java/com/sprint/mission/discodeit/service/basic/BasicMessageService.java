package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        // 메시지 생성을 위한 필수 검증
        validateCreateRequest(request);

        // 채널이 존재하는지 검증
        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 유저가 존재하는지 검증
        userRepository.findById(request.authorId())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        List<UUID> attachmentIds = new ArrayList<>();
        // 첨부파일 존재 여부 확인
        if (request.attachments() != null) {
            for (BinaryContentCreateRequest attachment : request.attachments()) {
                if (attachment == null) {
                    throw new RuntimeException("첨부파일이 올바르지 않습니다.");
                }

                // 첨부파일 저장
                BinaryContent saved = saveAttachment(attachment);
                attachmentIds.add(saved.getId());
            }
        }

        // 메시지 생성
        Message message = new Message(
                request.channelId(),
                request.authorId(),
                request.content(),
                List.copyOf(attachmentIds)
        );

        // 메시지 저장
        messageRepository.save(message);

        return MessageResponse.from(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        // 메시지를 조회하려는 채널이 존재하는지 검증
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 채널의 전체 메시지 목록 조회
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        List<MessageResponse> responses = new ArrayList<>();
        for (Message message : messages) {
            responses.add(MessageResponse.from(message));
        }

        return responses;
    }

    @Override
    public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
        // 메시지 수정을 위한 필수 검증
        validateUpdateRequest(messageId, request);

        // 수정 대상 메시지가 존재하는지 검증
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지가 존재하지 않습니다."));

        // 메시지 수정 및 저장
        message.update(request.newContent());
        messageRepository.save(message);

        return MessageResponse.from(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (messageId == null) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 삭제 대상 메시지가 존재하는지 검증
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지가 존재하지 않습니다."));

        // 메시지의 첨부파일이 존재하면 함께 삭제
        if (message.getAttachmentIds() != null) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                if (attachmentId != null) {
                    binaryContentRepository.delete(attachmentId);
                }
            }
        }

        // 메시지 삭제
        messageRepository.delete(messageId);
    }

    private void validateCreateRequest(MessageCreateRequest request) {
        if (request == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }
        if (request.channelId() == null) {
            throw new RuntimeException("채널이 필요합니다.");
        }
        if (request.authorId() == null) {
            throw new RuntimeException("작성자가 필요합니다.");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new RuntimeException("내용이 필요합니다.");
        }
    }

    private void validateUpdateRequest(UUID messageId, MessageUpdateRequest request) {
        if (request == null || messageId == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }
        if (request.newContent() == null || request.newContent().isBlank()) {
            throw new RuntimeException("내용이 필요합니다.");
        }
    }

    private BinaryContent saveAttachment(BinaryContentCreateRequest request) {
        if (request.bytes() == null || request.bytes().length == 0) {
            throw new RuntimeException("파일이 없습니다.");
        }
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
        return binaryContentRepository.save(binaryContent);
    }
}
