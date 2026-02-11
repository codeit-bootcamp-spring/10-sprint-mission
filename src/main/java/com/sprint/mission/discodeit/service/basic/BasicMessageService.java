package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AttachmentCreateRequestDTO;
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

//    @RequiredArgsConstructor로 대체
//    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository) {
//        this.messageRepository = messageRepository;
//        this.channelRepository = channelRepository;
//        this.userRepository = userRepository;
//    }

    @Override
    public MessageResponseDTO create(MessageCreateRequestDTO messageCreateRequestDTO) {
        String content = messageCreateRequestDTO.content();
        UUID channelId= messageCreateRequestDTO.channelId();
        UUID authorId= messageCreateRequestDTO.authorId();
        List<AttachmentCreateRequestDTO> attachments = messageCreateRequestDTO.attachments();
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
        List<UUID> newAttachmentIds = toAttachmentIds(messageUpdateRequestDTO.attachments());
        message.update(messageUpdateRequestDTO.newContent(), newAttachmentIds);
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
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }
    
    // Message create,update요청시 List<AttachmentCreateRequestDTO> attachments를 통해 BinaryContent를 생성하고
    // Message의 필드로 들어갈 List<UUID> attachmentIds를 반환하는 메서드
    private List<UUID> toAttachmentIds(List<AttachmentCreateRequestDTO> attachments) {
        List<UUID> attachmentIds = new ArrayList<>();
        if (!attachments.isEmpty()) {
            for (AttachmentCreateRequestDTO attachmentCreateRequestDTO : attachments) {
                byte[] bytes = attachmentCreateRequestDTO.bytes();
                String contentType = attachmentCreateRequestDTO.contentType();
                BinaryContent attachment = binaryContentRepository.save(new BinaryContent(contentType, bytes));
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
