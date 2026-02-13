package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.*;
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
    //
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final MessageMapper messageMapper;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public MessageResponseDto create(UUID channelId, UUID authorId, MessageCreateDto dto, List<BinaryContentCreateDto> binaryContentCreateDtos) {
        checkMember(channelId, authorId);
        checkValidate(dto, binaryContentCreateDtos);
        List<UUID> attachmentIds = new ArrayList<>();
        binaryContentCreateDtos
                .forEach((attachment) -> {
                    BinaryContent content = binaryContentRepository.save(binaryContentMapper.toEntity(attachment));
                    attachmentIds.add(content.getId());
                });
        Message message = messageMapper.toEntity(dto, attachmentIds, authorId, channelId);
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = get(messageId);
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
        Message message = get(id);
        checkMember(message.getChannelId(), userId);
        checkAuthor(message.getAuthorId(), userId);
        message.update(dto.content(),null);//첨부파일 변경을 하려면 별도로 메서드 필요
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public void delete(UUID messageId, UUID userId) {
        Message message = get(messageId);
        checkMember(message.getChannelId(), userId);
        checkAuthor(message.getAuthorId(), userId);
        if(message.getAttachmentIds()!=null && !message.getAttachmentIds().isEmpty()){
            message.getAttachmentIds().forEach(binaryContentRepository::delete);//첨부파일 있는경우만 지우기
        }
        messageRepository.deleteById(messageId);

    }
    private void checkValidate(MessageCreateDto dto,List<BinaryContentCreateDto> binaryContentCreateDtos) {
        if((dto.content()==null || dto.content().isEmpty()) //컨텐츠와 첨부파일 두개다 없는 경우
                && (binaryContentCreateDtos==null || binaryContentCreateDtos.isEmpty()) ){
            throw new BusinessLogicException(ExceptionCode.INVALID_MESSAGE);
        }
    }
    private void checkMember(UUID channelId, UUID userId){
        if (!channelRepository.existsById(channelId)) {
            throw new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND);
        }
        if (!userRepository.existsById(userId)) {//나중에 인증 하면 필요 없음
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
        if (readStatusRepository.findByUserIdAndChannelId(userId,channelId).isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND);
        }
    }
    private Message get(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));
    }
    private void checkAuthor(UUID authorId, UUID userId) {
        if(!authorId.equals(userId)) {
            throw new BusinessLogicException(ExceptionCode.MESSAGE_AUTHOR_ONLY);
        }
    }
}
