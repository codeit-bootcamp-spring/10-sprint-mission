package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.file.FileUploadFailException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments) {
        User user = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new UserNotFoundException(request.getAuthorId()));
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new ChannelNotFoundException(request.getChannelId()));
        // 메시지 객체 생성
        Message message = new Message(user,request.getContent(),channel);
        if(attachments == null){
            attachments = new ArrayList<>();
        }

        attachments.forEach(bc -> {
            BinaryContent binaryContent;
            try {
                binaryContent = new BinaryContent(
                        bc.getSize(),
                        bc.getOriginalFilename(),
                        bc.getContentType());
                binaryContent = binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bc.getBytes());
            } catch (IOException e) {
                throw new FileUploadFailException();
            }
            message.addAttachment(binaryContent);
            // 바이너리 컨텐츠는 조인 테이블의 casecade.All로 인해서 저장안해도됨
        });

        // 데이터에 정보 저장
        messageRepository.save(message);
        log.info("메시지 생성 성공: 메시지 id = {}, 첨부파일 수 = {}", message.getId(), attachments.size());
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto findMessage(UUID messageId) {
        Message message = getMessage(messageId);
        log.trace("메시지 조회 성공: 메시지 id = {}", messageId);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllMessagesByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        // slice는 페이자(offset)이 항상 0이어야함
        Pageable safePageable = PageRequest.of(0, pageable.getPageSize(), pageable.getSort());
        Slice<Message> messageSlice;

        if(cursor == null){
            messageSlice = messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, safePageable);
        } else{
            messageSlice = messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(channelId, cursor, safePageable);
        }

        Slice<MessageDto> dtoSlice = messageSlice.map(messageMapper::toDto);

        Instant nextCursor = null;
        if (dtoSlice.hasNext() && !dtoSlice.getContent().isEmpty()) {
            // 데이터가 더 남아있다면, 지금 리스트의 제일 마지막(가장 오래된) 메시지의 시간을 다음 커서로 지정
            int lastIndex = dtoSlice.getContent().size() - 1;
            nextCursor = dtoSlice.getContent().get(lastIndex).getCreatedAt();
        }

        return pageResponseMapper.fromSlice(dtoSlice, nextCursor);
    }

    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest dto) {
        Message message = getMessage(messageId);
        message.updateMessage(dto.getNewContent());

        log.info("메시지 수정 성공: 메시지 id = {}", messageId);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = getMessage(messageId);

        //데이터에서 메시지 삭제 -> 조인 테이블과의 영속성 전이로 인해 관련된 조인 테이블의 데이터도 삭제 -> binaryContent와 조인테이블
        // 과의 연결도 끊김 -> JPA는 조인 테이블과의 연결이 끊긴 데이터를 orphan으로 인식하고 삭제해버림(orphanRemoval)
        messageRepository.delete(message);
        log.info("메시지 삭제 성공: 메시지 id = {}", messageId);
    }

    // 유효성 검사
    private Message getMessage(UUID messageId){
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
    }

}
