package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
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
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponseDTO createMessage(CreateMessageRequestDTO dto) {
        User user = findUserOrThrow(dto.sentUserId());
        Channel channel = findChannelOrThrow(dto.sentChannelId());
        List<UUID> attachments = new ArrayList<>();
        // 껍데기 생성
        Message message = MessageMapper.toEntity(dto, attachments);

        List<CreateBinaryContentRequestDTO> attachmentDTOs = dto.attachmentDTOs();

        if (attachmentDTOs != null && !attachmentDTOs.isEmpty()) {
            for (CreateBinaryContentRequestDTO bcDTO: attachmentDTOs) {
                checkDTOHasNull(bcDTO);

                BinaryContent bc
                        = BinaryContentMapper.toEntity(user.getId(), message.getId(), bcDTO);

                binaryContentRepository.save(bc);

                attachments.add(bc.getId());
            }

        }
        // 영속화
        messageRepository.save(message);

        return MessageMapper.toResponse(message);
    }

    @Override
    public List<MessageResponseDTO> findAllByUserId(UUID userId) {
        findUserOrThrow(userId);
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getSentUserId().equals(userId))
                .toList();

        return MessageMapper.toResponseList(messages);
    }

    @Override
    public List<MessageResponseDTO> findAllByChannelId(UUID channelId) {
        findChannelOrThrow(channelId);

        return MessageMapper.toResponseList(messageRepository.findByChannelId(channelId));
    }

    @Override
    public MessageResponseDTO findByMessageId(UUID messageId) {
        return MessageMapper.toResponse(findMessageOrThrow(messageId));
    }

    @Override
    public MessageResponseDTO updateMessage(UpdateMessageRequestDTO dto) {
        Message message = findMessageOrThrow(dto.messageId());

        if (dto.content() == null) {
            throw new IllegalArgumentException("content는 null값일 수 없습니다.");
        }

        message.updateContent(dto.content());
        messageRepository.save(message);

        return MessageMapper.toResponse(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        List<UUID> ids = findMessageOrThrow(messageId).getAttachmentIds();

        for (UUID id: ids) {
            binaryContentRepository.deleteById(id);
        }

        messageRepository.deleteById(messageId);
    }

    private Message findMessageOrThrow(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 null값일 수 없습니다.");

        return messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 메시지가 존재하지 않습니다."));
    }

    private Channel findChannelOrThrow(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        return channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다."));
    }

    private User findUserOrThrow(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 id를 가진 사용자가 존재하지 않습니다."));
    }

    private void checkDTOHasNull(CreateBinaryContentRequestDTO dto) {
        if(dto == null) {
            throw new IllegalArgumentException("bcDTO는 null일 수 없습니다.");
        }

        if (dto.data() == null) {
            throw new IllegalArgumentException("data의 값은 null일 수 없습니다.");
        }

        if (dto.contentType() == null) {
            throw new IllegalArgumentException("contentType은 null일 수 없습니다.");
        }

        if (dto.filename() == null) {
            throw new IllegalArgumentException("filename은 null일 수 없습니다.");
        }
    }
}
