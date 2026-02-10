package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 메시지 생성
    @Override
    public MessageResponseDTO create(MessageCreateRequestDTO messageCreateRequestDTO) {
        // 1, 사용자 및 채널 존재 여부 확인
        userRepository.findById(messageCreateRequestDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        channelRepository.findById(messageCreateRequestDTO.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        // 2. 메시지 생성 및 저장
        Message newMessage = new Message(messageCreateRequestDTO);
        messageRepository.save(newMessage);

        // 3. 첨부파일 선택적 생성 및 저장
        Optional.ofNullable(messageCreateRequestDTO.getBinaryContentCreateRequestDTOList())
                .map(binaryContentCreateRequestDTOS -> binaryContentCreateRequestDTOS.stream()
                        // 첨부파일 하나씩 생성 및 저장
                        .map(binaryContentCreateRequestDTO -> {
                            BinaryContent binaryContent = new BinaryContent(binaryContentCreateRequestDTO);
                            binaryContentRepository.save(binaryContent);
                            return binaryContent.getId();
                        })
                        // 첨부파일 식별자 목록 반환
                        .toList())
                // 생성한 메시지의 첨부파일 목록에 생성한 첨부파일 추가
                .ifPresent(attachmentIds -> attachmentIds
                        .forEach(newMessage::addAttachment));

        // 4. 응답 DTO 생성 및 반환
        return toResponseDTO(newMessage);
    }

    // 메시지 단건 조회
    @Override
    public MessageResponseDTO findById(UUID targetMessageId) {
        // 1. 메시지 존재 여부 확인
        Message targetMessage = messageRepository.findById(targetMessageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));

        // 2. 응답 DTO 생성 및 반환
        return toResponseDTO(targetMessage);
    }

    // 메시지 전체 조회
    @Override
    public List<MessageResponseDTO> findAll() {
        return messageRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 특정 채널의 전체 메시지 목록 조회
    @Override
    public List<MessageResponseDTO> findAllByChannelId(UUID channelId) {
        // 1. 채널 존재 여부 확인
        Channel taregetChannel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        // 2. 특정 채널의 전체 메시지 응답 DTO 변환 및 반환
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(taregetChannel.getId()))
                // 메시지 -> 메시지 응답 반환
                .map(this::toResponseDTO)
                .toList();
    }

    // 특정 사욪자가 발행한 전체 메시지 목록 조회
    @Override
    public List<MessageResponseDTO> findAllByUserId(UUID targetUserId) {
        // 1. 사용자 존재 여부 확인
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        // 2. 특정 사용자가 발행한 전체 메시지 응답 DTO 변환 및 반환
        return messageRepository.findAll().stream()
                .filter(message -> message.getAuthorId().equals(targetUser.getId()))
                .map(this::toResponseDTO)
                .toList();
    }

    // 메시지 수정
    @Override
    public MessageResponseDTO update(MessageUpdateRequestDTO messageUpdateRequestDTO) {
        // 1. 메시지 존재 여부 확인
        Message targetMessage = findMessageEntityById(messageUpdateRequestDTO.getId());

        // 2. 메시지 내용 수정
        Optional.ofNullable(messageUpdateRequestDTO.getMessage())
                .ifPresent(message -> {
                    validateString(message, "[메시지 변경 실패] 올바른 메시지 형식이 아닙니다.");
                    validateDuplicateValue(targetMessage.getMessage(), message, "[메시지 변경 실패] 이전 메시지와 동일합니다.");
                    targetMessage.updateMessage(messageUpdateRequestDTO.getMessage());
                });

        // 3. 메시지 수정 내용 저장 및 응답 DTO 반환
        messageRepository.save(targetMessage);
        return toResponseDTO(targetMessage);
    }

    // 메시지 삭제
    @Override
    public void delete(UUID targetMessageId) {
        Message targetMessage = findMessageEntityById(targetMessageId);

        targetMessage.getAttachmentIds()
                .forEach(binaryContentId -> {
                    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                            .orElseThrow(() -> new IllegalArgumentException("해당 첨부 파일이 존재하지 않습니다."));
                    binaryContentRepository.delete(binaryContent);
                });

        messageRepository.delete(targetMessage);
    }

    // 메시지 엔티티 반환
    public Message findMessageEntityById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
    }

    // 응답 DTO 변환
    public MessageResponseDTO toResponseDTO(Message message) {
        return MessageResponseDTO.builder()
                .id(message.getId())
                .authorId(message.getAuthorId())
                .channelId(message.getChannelId())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .messageType(message.getMessageType())
                .attachmentIds(message.getAttachmentIds())
                .build();
    }
}
