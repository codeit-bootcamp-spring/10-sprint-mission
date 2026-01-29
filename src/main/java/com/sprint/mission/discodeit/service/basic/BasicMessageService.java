package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
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

import java.util.Collection;
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
    public MessageResponseDTO createMessage(MessageCreateRequestDTO messageCreateRequestDTO) {
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
    public Message searchMessage(UUID targetMessageId) {
        return messageRepository.findById(targetMessageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
    }

    // 메시지 전체 조회
    @Override
    public List<Message> searchMessageAll() {
        return messageRepository.findAll();
    }

    // 특정 유저가 발행한 메시지 다건 조회
    public List<Message> searchMessagesByUserId(UUID targetUserId) {
        userRepository.findById(targetUserId).orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        List<Message> messages = searchMessageAll();               // 함수가 실행된 시점에서 가장 최신 메시지 목록

        return messages.stream()
                .filter(message -> message.getAuthorId().equals(targetUserId))
                .toList();
    }

    // 특정 채널의 메시지 발행 리스트 조회
    public List<Message> searchMessagesByChannelId(UUID targetChannelId) {
        channelRepository.findById(targetChannelId).orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        List<Message> messages = searchMessageAll();

        return messages.stream()
                .filter(message -> message.getChannelId().equals(targetChannelId))
                .toList();
    }

    // 메시지 수정
    @Override
    public Message updateMessage(UUID targetMessageId, String newMessage) {
        Message targetMessage = searchMessage(targetMessageId);

        // 메시지 내용 수정
        Optional.ofNullable(newMessage)
                .ifPresent(message -> {
                    validateString(message, "[메시지 변경 실패] 올바른 메시지 형식이 아닙니다.");
                    validateDuplicateValue(targetMessage.getMessage(), message, "[메시지 변경 실패] 이전 메시지와 동일합니다.");
                    targetMessage.updateMessage(newMessage);
                });

        messageRepository.save(targetMessage);
        return targetMessage;
    }

    @Override
    public void updateMessage(UUID channelId, Channel channel) {}

    // 메시지 삭제
    @Override
    public void deleteMessage(UUID targetMessageId) {
        Message targetMessage = searchMessage(targetMessageId);

        messageRepository.delete(targetMessage);
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
                .build();
    }
}
