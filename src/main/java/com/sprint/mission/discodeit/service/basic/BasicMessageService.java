package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    //
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final MessageMapper messageMapper;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public MessageResponseDto create(UUID channelId, UUID authorId, MessageCreateDto dto) {
        checkMember(channelId, authorId);
        checkValidate(dto);
        List<UUID> attachmentIds = new ArrayList<>();
        if(dto.attachments()!=null && !dto.attachments().isEmpty()){
            dto.attachments()
                    .forEach((attachment) -> {
                        BinaryContent content = binaryContentRepository.save(binaryContentMapper.toEntity(attachment));
                        attachmentIds.add(content.getId());
                    });
        }
        Message message = messageMapper.toEntity(dto, attachmentIds);
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        return messageMapper.toDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID userId, UUID channelId) {
        checkMember(channelId, userId);
        List<MessageResponseDto> response = new ArrayList<>();
        messageRepository.findAllByChannelId(channelId)
                .forEach(message -> response.add(messageMapper.toDto(message)));
        return response;
    }

    @Override
    public MessageResponseDto update(UUID id,UUID userId, MessageUpdateDto dto) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
        checkMember(message.getChannelId(), userId);
        if(!message.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("작성자가 아님");
        }
        message.update(dto.content(),null);//첨부파일 변경을 하려면 별도로 메서드 필요
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public void delete(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                        .orElseThrow(()-> new NoSuchElementException("Message with id " + messageId + " not found"));
        checkMember(message.getChannelId(), userId);
        if(!message.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("작성자가 아님");
        }
        if(message.getAttachmentIds()!=null && !message.getAttachmentIds().isEmpty()){
            message.getAttachmentIds().forEach(binaryContentRepository::delete);//첨부파일 있는경우만 지우기
        }
        messageRepository.deleteById(messageId);

    }
    private void checkValidate(MessageCreateDto dto) {
        if((dto.content()==null || dto.content().isEmpty()) //컨텐츠와 첨부파일 두개다 없는 경우
                && (dto.attachments()==null || dto.attachments().isEmpty()) ){
            throw new IllegalArgumentException("Content or attachment not found");
        }
    }
    private void checkMember(UUID channelId, UUID userId){
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }
        if (!userRepository.existsById(userId)) {//나중에 인증 하면 필요 없음
            throw new NoSuchElementException("Author not found with id " + userId);
        }
        if (readStatusRepository.findByUserIdAndChannelId(userId,channelId).isEmpty()) {
            throw new NoSuchElementException("Not Member of this Channel");
        }
    }
}
