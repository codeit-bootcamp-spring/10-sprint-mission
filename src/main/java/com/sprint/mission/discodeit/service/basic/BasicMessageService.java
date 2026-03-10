package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
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
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    // 메세지 생성 메소드
    // 선택적으로 첨부 파일(BinaryContent)를 여러 개 등록할 수 있다.
    @Transactional
    @Override
    public MessageDto create(List<MultipartFile> profiles, MessageCreateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");
        Objects.requireNonNull(req.channelId(), "유효하지 않은 채널ID 입니다!");
        Objects.requireNonNull(req.authorId(), "유효하지 않은 사용자ID 입니다!");

        Channel channel = channelRepository.findById(req.channelId())
            .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다!"));
        User user = userRepository.findById(req.authorId())
            .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다!"));

        List<BinaryContent> profileList = new ArrayList<>();

        if (profiles != null) {
            for (MultipartFile profile : profiles) {
                BinaryContent saved = binaryContentRepository.save(
                    new BinaryContent(
                        profile.getName(),
                        profile.getSize(),
                        profile.getContentType()
                    )
                );
                profileList.add(saved);

                try {
                    binaryContentStorage.put(saved.getId(), profile.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Byte 저장 실패");
                }

            }
        }

        Message message = new Message(req.content(), channel, user, profileList);
        Message saved = messageRepository.save(message);

        return messageMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        Objects.requireNonNull(messageId, "유효하지 않은 메시지ID 입니다.");

        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new IllegalStateException("존재하지 않는 메시지입니다.")
            );

        return messageMapper.toDto(message);


    }

    @Transactional
    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "유효하지 않은 채널id 입니다.");

        List<Message> messages = messageRepository.findByChannelId(channelId);

        return messages
            .stream()
            .map(
                messageMapper::toDto
            ).toList();

    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Optional<Instant> cursor,
        Pageable pageable) {
        Objects.requireNonNull(channelId, "유효하지 않은 채널 식별자!");
        Objects.requireNonNull(cursor, "유효하지 않은 cursor!");
        Objects.requireNonNull(pageable, "유효하지 않은 페이징 정보!");

        if (cursor.isEmpty()) {
            throw new NoSuchElementException("조회할 커서가 없습니다");
        }

        Slice<Message> slice = messageRepository.findByChannelIdAndCursor(channelId, cursor.get(),
            pageable);
        Slice<MessageDto> dtoSlice = slice.map(messageMapper::toDto);

        return pageResponseMapper.fromSlice(dtoSlice);

    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequestDto req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");
        Objects.requireNonNull(messageId, "유효하지 않은 메시지 식별자!");

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException(
                "Message with id " + messageId + " not found"));

        if (req.newContent() != null) {
            message.setContent(req.newContent());
        }

        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        Objects.requireNonNull(messageId, "유효하지 않은 메시지 식별자!");

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다!"));

        messageRepository.delete(message);
    }
}
