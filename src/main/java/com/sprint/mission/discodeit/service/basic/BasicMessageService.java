package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.message.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentFileProcessingErrorException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    private final BinaryContentStorage binaryContentStorage;

    // 메시지 생성
    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments) {
        UserEntity targetUser = getUserEntityOrThrow(messageCreateRequest.authorId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(messageCreateRequest.channelId());

        MessageEntity newMessage = messageMapper.toEntity(messageCreateRequest, targetUser, targetChannel);
        createAttachments(newMessage, attachments);

        messageRepository.save(newMessage);

        log.info("[MESSAGE_CREATE] 메시지 생성 완료: id={}, authorId={}, channelId={}, attachments= 총 {}개",
                newMessage.getId(),
                newMessage.getAuthor().getId(),
                newMessage.getChannel().getId(),
                newMessage.getAttachments().size()
        );
        return messageMapper.toDto(newMessage);
    }

    // 메시지와 함께 전송된 첨부 파일 생성 및 저장
    private void createAttachments (MessageEntity newMessage, List<MultipartFile> attachments) {
        // 메시지와 함께 전송된 첨부 파일이 Null 일 경우, 빈 리스트 반환
        List<MultipartFile> attachmentsOfUser = (attachments == null)
                ? List.of()
                : attachments;

        for (MultipartFile file : attachmentsOfUser) {
            try {
                BinaryContentEntity newBinaryContent = new BinaryContentEntity(
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType());

                binaryContentRepository.save(newBinaryContent);
                binaryContentStorage.put(newBinaryContent.getId(), file.getBytes());

                // BinaryContent - Message 간 연관 관계 설정
                newMessage.addAttachment(newBinaryContent);
            } catch (Exception e) {
                throw new BinaryContentFileProcessingErrorException(newMessage.getId(), file.getName());
            }
        }
    }

    // 메시지 단건 조회
    @Override
    public MessageDto findById(UUID messageId) {
       MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

       return messageMapper.toDto(targetMessage);
    }

    // 메시지 전체 조회
    @Override
    public List<MessageDto> findAll() {
        return messageRepository.findAllWithDetails().stream()
                .map(messageMapper::toDto)
                .toList();
    }

    // 특정 채널에서 발행된 메시지 목록 조회
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
        getChannelEntityOrThrow(channelId);

        // 페이지네이션에 포함할 메시지 목록 조회
        List<MessageEntity> messages = fetchMessages(channelId, cursor, size);

        // 페이징 계산
        boolean hasNext = messages.size() > size;
        List<MessageEntity> pagedMessages = hasNext
                ? messages.subList(0, size)         // 10개씩 자르기
                : messages;

        String nextCursor = calculateNextCursor(pagedMessages, hasNext);

        // 페이징 전용 응답 DTO 조립 및 반환
        return buildPageResponse(channelId, pagedMessages, nextCursor, hasNext);
    }

    // 페이지네이션에 포함될 메시지 목록 조회
    private List<MessageEntity> fetchMessages(UUID channelId, Instant cursor, int size) {
        // 다음 페이지 여부 확인을 위해 size + 1개 조회
        Pageable limit = PageRequest.of(0, size + 1);

        // 커서 유무에 따른 메시지 목록 조회
        return cursor == null
                ? messageRepository.findFirstPageByChannelId(channelId, limit)
                : messageRepository.findNextPageByChannelId(channelId, cursor, limit);
    }

    // 다음 페이지 시작점 (커서) 계산
    private String calculateNextCursor(List<MessageEntity> pagedMessages, boolean hasNext) {
        if (!hasNext || pagedMessages.isEmpty()) {
            return null;
        }

        return pagedMessages.get(pagedMessages.size() - 1).getCreatedAt().toString();
    }

    // 페이징 전용 응답 객체 조립
    private PageResponse<MessageDto> buildPageResponse(UUID channelId, List<MessageEntity> pagedMessages, String nextCursor, boolean hasNext) {
        long totalElements = messageRepository.countByChannelId(channelId);

        // 엔티티 -> 기본 응답 DTO
        List<MessageDto> messageDtoList = pagedMessages.stream()
                .map(messageMapper::toDto)
                .toList();

        // 기본 응답 DTO -> 페이징 전용 DTO
        return pageResponseMapper.fromCursor(
                messageDtoList,
                nextCursor,
                messageDtoList.size(),
                hasNext,
                totalElements
        );
    }

    // 특정 사용자가 발행한 전체 메시지 목록 조회
    @Override
    public List<MessageDto> findAllByUserId(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return messageRepository.findByAuthor(targetUser).stream()
                .map(messageMapper::toDto)
                .toList();
    }

    // 메시지 수정
    @Override
    @PreAuthorize("@authValidator.isMessageOwner(#messageId, authentication.name)")
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

        // 메시지 덮어쓰기
        targetMessage.updateMessage(messageUpdateRequest.newContent());

        log.info("[MESSAGE_UPDATE] 메시지 수정 완료: id={}",targetMessage.getId());
        return messageMapper.toDto(targetMessage);
    }

    // 메시지 삭제
    @Override
    @PreAuthorize("@authValidator.isMessageOwner(#messageId, authentication.name)")
    @Transactional
    public void delete(UUID messageId) {
        MessageEntity targetMessage = getMessageEntityOrThrow(messageId);

        messageRepository.delete(targetMessage);
        log.info("[MESSAGE_DELETE] 메시지 삭제 완료: id={}", targetMessage.getId());
    }

    // 사용자 반환
    private UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // 채널 반환
    private ChannelEntity getChannelEntityOrThrow(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    // 메시지 반환
    private MessageEntity getMessageEntityOrThrow(UUID messageId){
        return messageRepository.findWithDetails(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
    }
}