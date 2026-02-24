package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponseDTO create(MessageCreateRequestDTO messageCreateRequestDTO,
                                     Optional<List<BinaryContentCreateRequestDTO>> binaryContentCreateRequestDTO) {
        String content = messageCreateRequestDTO.content();
        UUID channelId= messageCreateRequestDTO.channelId();
        UUID authorId= messageCreateRequestDTO.authorId();
        List<BinaryContentCreateRequestDTO> attachments = binaryContentCreateRequestDTO.orElse(new ArrayList<>());
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException(channelId+"를 가진 채널이 없습니다");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException(authorId+"를 가진 유저가 없습니다");
        }
        List<UUID> attachmentIds = toAttachmentIds(attachments);
        Message newMessage = new Message(content, channelId, authorId, attachmentIds);
        return toMessageResponseDTO(messageRepository.save(newMessage));
    }

    @Override
    public MessageResponseDTO find(UUID messageId) {
        Message message = getMessageByIdOrThrow(messageId);
        return toMessageResponseDTO(message);
    }

    @Override
    public List<MessageResponseDTO> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toMessageResponseDTO)
                .toList();
    }

    @Override
    public MessageResponseDTO update(UUID messageId, MessageUpdateRequestDTO messageUpdateRequestDTO) {
        Message message = getMessageByIdOrThrow(messageId);
        message.update(messageUpdateRequestDTO.newContent(), message.getAttachmentIds());
        return toMessageResponseDTO(messageRepository.save(message));
    }

    @Override
    public void delete(UUID messageId) {
        Message message = getMessageByIdOrThrow(messageId);
        // 관련 도메인 삭제 - 첨부파일(BinaryContent)
        if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
            message.getAttachmentIds()
                    .forEach(binaryContentRepository::deleteById);
        }
        messageRepository.deleteById(messageId);
    }
    
    // 간단한 응답용 DTO를 만드는 메서드
    private MessageResponseDTO toMessageResponseDTO(Message message) {
        return new MessageResponseDTO(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }
    
    // Message create요청시 List<BinaryContentCreateRequestDTO> attachments를 통해 BinaryContent를 생성하고
    // Message의 필드로 들어갈 List<UUID> attachmentIds를 반환하는 메서드
    private List<UUID> toAttachmentIds(List<BinaryContentCreateRequestDTO> attachments) {
        List<UUID> attachmentIds = new ArrayList<>();
        if (!attachments.isEmpty()) {
            for (BinaryContentCreateRequestDTO binaryContentCreateRequestDTO : attachments) {
                String fileName = binaryContentCreateRequestDTO.fileName();
                byte[] bytes = binaryContentCreateRequestDTO.content();
                String contentType = binaryContentCreateRequestDTO.contentType();
                BinaryContent attachment = binaryContentRepository.save(new BinaryContent(fileName, contentType, bytes));
                attachmentIds.add(attachment.getId());
            }
        }
        return attachmentIds;
    }

    // MessageRepository.findById()를 통한 반복되는 Message 조회/예외처리를 중복제거 하기 위한 메서드
    private Message getMessageByIdOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("messageId:"+messageId+"를 가진 메시지를 찾지 못했습니다"));
    }
}
