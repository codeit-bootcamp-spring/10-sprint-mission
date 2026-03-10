package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest,
                             Optional<List<BinaryContentCreateRequest>> binaryContentCreateRequestList) {
        String content = messageCreateRequest.content();
        List<BinaryContentCreateRequest> attachmentList = binaryContentCreateRequestList.orElse(new ArrayList<>());

        UUID channelId = messageCreateRequest.channelId();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException(channelId+"를 가진 채널이 없습니다"));
        UUID authorId = messageCreateRequest.authorId();
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException(authorId+"를 가진 유저가 없습니다"));

        List<BinaryContent> attachments = new ArrayList<>();
        for (BinaryContentCreateRequest attachmentDto : attachmentList) {
            BinaryContent attachment = new BinaryContent(
                    attachmentDto.fileName(),
                    (long)attachmentDto.bytes().length,
                    attachmentDto.contentType()
            );
            attachments.add(attachment);
            binaryContentRepository.save(attachment);
            // attachment를 save해야 id가 생기게 되고 attachment.getId()를 할 수가 있음
            binaryContentStorage.put(attachment.getId(), attachmentDto.bytes());
        }
        Message newMessage = new Message(content, channel, author, attachments);
        return messageMapper.toDto(messageRepository.save(newMessage));
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {
        Message message = getMessageByIdOrThrow(messageId);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
        Page<MessageDto> messagePage = messageRepository.findAllByChannelId(channelId, pageable)
                .map(messageMapper::toDto);
        return pageResponseMapper.fromPage(messagePage);
    }

    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        Message message = getMessageByIdOrThrow(messageId);
        message.update(messageUpdateRequest.newContent(), message.getAttachments());
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public void delete(UUID messageId) {
        Message message = getMessageByIdOrThrow(messageId);
        // 관련 도메인 삭제 - 첨부파일(BinaryContent)
        messageRepository.deleteById(messageId);// 이때 binaryContent도 삭제가 되는지?
    }

    // MessageRepository.findById()를 통한 반복되는 Message 조회/예외처리를 중복제거 하기 위한 메서드
    private Message getMessageByIdOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("messageId:"+messageId+"를 가진 메시지를 찾지 못했습니다"));
    }
}
