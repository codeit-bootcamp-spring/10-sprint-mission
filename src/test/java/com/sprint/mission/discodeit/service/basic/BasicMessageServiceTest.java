package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.MessageSentEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.InvalidMessageContentException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private ChannelRepository channelRepository;
    @Mock private UserRepository userRepository;
    @Mock private BinaryContentRepository binaryContentRepository;
    @Mock private BinaryContentStorage binaryContentStorage;
    @Mock private MessageMapper messageMapper;
    @Mock private PageResponseMapper pageResponseMapper;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BasicMessageService basicMessageService;



    @Test
    @DisplayName("유효한 데이터로 메시지를 성공적으로 생성하고 이벤트를 발행합니다.")
    void create_success() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        CreateMessageRequestDto request = new CreateMessageRequestDto("content created", channelId, authorId, null);

        Channel channel = new Channel(ChannelType.PUBLIC, "Test Channel", "Desc");
        User author = new User("tester", "test@test.com", "pass");
        Message message = new Message(request.content(), channel, author);

        UserDto authorDto = new UserDto(authorId, author.getUsername(), author.getEmail(), null, Role.USER, true);
        MessageDto expectedDto = new MessageDto(UUID.randomUUID(), request.content(), channelId, authorDto, List.of(), Instant.now(), Instant.now());

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

        // when
        MessageDto result = basicMessageService.create(request, null);

        // then
        assertEquals(expectedDto, result);
        verify(messageRepository).save(any(Message.class));
        verify(eventPublisher).publishEvent(any(MessageSentEvent.class)); // 이벤트 발행 검증
    }

    @Test
    @DisplayName("메시지 내용과 첨부파일이 모두 없는 경우 예외가 발생합니다.")
    void create_fail_invalid_content() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        CreateMessageRequestDto request = new CreateMessageRequestDto("   ", channelId, authorId, null);

        Channel channel = new Channel(ChannelType.PUBLIC, "Test Channel", "Desc");
        User author = new User("tester", "test@test.com", "pass");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));

        // when & then
        assertThrows(InvalidMessageContentException.class, () -> basicMessageService.create(request, null));
    }


    @Test
    @DisplayName("존재하는 메시지의 텍스트 내용을 성공적으로 수정합니다.")
    void update_success() {
        // given
        UUID messageId = UUID.randomUUID();
        UpdateMessageRequestDto request = new UpdateMessageRequestDto("Updated Content");
        Message message = new Message("Old Content", new Channel(ChannelType.PUBLIC, "C", "D"), new User("jiho", "jiho@codeit.kr", "asdf1234"));

        UserDto authorDto = new UserDto(UUID.randomUUID(), "jiho", "jiho@codeit.kr", null, Role.USER, true);
        MessageDto expectedDto = new MessageDto(messageId, request.newContent(), UUID.randomUUID(), authorDto, List.of(), Instant.now(), Instant.now());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(expectedDto);

        // when
        MessageDto result = basicMessageService.update(messageId, request);

        // then
        assertEquals("Updated Content", message.getContent());
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("존재하지 않는 메시지를 수정하려고 하면 예외가 발생합니다.")
    void update_fail_message_not_found() {
        // given
        UUID notFoundMessageId = UUID.randomUUID();
        UpdateMessageRequestDto request = new UpdateMessageRequestDto("Updated Content");

        given(messageRepository.findById(notFoundMessageId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MessageNotFoundException.class, () -> basicMessageService.update(notFoundMessageId, request));
    }


    @Test
    @DisplayName("메시지와 연관된 첨부파일을 해제하고 모두 삭제합니다.")
    void delete_success() {
        // given
        UUID messageId = UUID.randomUUID();
        Message message = new Message("Content", new Channel(ChannelType.PUBLIC, "Test", "test description"), new User("jiho", "jiho@ab.kr", "sdf234"));
        ReflectionTestUtils.setField(message, "id", messageId);

        // 가짜 파일 생성 및 주입
        BinaryContent attachment = new BinaryContent("image.png", "image/png", 1024L);
        ReflectionTestUtils.setField(attachment, "id", UUID.randomUUID());
        message.addAttachment(attachment);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        basicMessageService.delete(messageId);

        // then
        verify(messageRepository).save(message);
        verify(messageRepository).flush();       // detachAttachments 로직 내의 flush 호출 검증
        verify(binaryContentRepository).deleteById(attachment.getId());
        verify(messageRepository).delete(message);
    }

    @Test
    @DisplayName("존재하지 않는 메시지를 삭제하려고 하면 예외가 발생합니다.")
    void delete_fail_message_not_found() {
        // given
        UUID notFoundMessageId = UUID.randomUUID();
        given(messageRepository.findById(notFoundMessageId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MessageNotFoundException.class, () -> basicMessageService.delete(notFoundMessageId));
    }


    @Test
    @DisplayName("채널 내의 메시지 목록을 성공적으로 페이징 조회합니다.")
    void find_all_by_channel_id_success() {
        // given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Message message = new Message("Content", new Channel(ChannelType.PUBLIC, "C", "D"), new User("jiho", "jiho@page.kr", "sdf234"));
        Slice<Message> slice = new SliceImpl<>(List.of(message), pageable, false);

        UserDto authorDto = new UserDto(UUID.randomUUID(), "jiho", "jiho@page.kr", null, Role.USER, true);
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), "Content", channelId, authorDto, List.of(), Instant.now(), Instant.now());
        PageResponse<MessageDto> expectedResponse = new PageResponse<>(List.of(messageDto), null, 10, false);

        given(channelRepository.existsById(channelId)).willReturn(true);
        given(messageRepository.findByChannelId(channelId, pageable)).willReturn(slice);
        given(messageMapper.toDto(any(Message.class))).willReturn(null);
        given(pageResponseMapper.fromSlice(any(Slice.class), any())).willReturn(expectedResponse);

        // when
        PageResponse<MessageDto> result = basicMessageService.findAllByChannelId(channelId, null, pageable);

        // then
        assertEquals(expectedResponse, result);
    }

    @Test
    @DisplayName("존재하지 않는 채널의 메시지 목록을 조회하려고 하면 예외가 발생합니다.")
    void find_all_by_channel_id_fail_channel_not_found() {
        // given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        given(channelRepository.existsById(channelId)).willReturn(false);

        // when & then
        assertThrows(ChannelNotFoundException.class, () -> basicMessageService.findAllByChannelId(channelId, null, pageable));
    }

    @Test
    @DisplayName("존재하지 않는 채널에 메시지를 생성하려고 하면 ChannelNotFoundException 예외가 발생한다.")
    void create_message_channel_not_found_exception() {
        // given
        UUID notFoundChannelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        CreateMessageRequestDto request = new CreateMessageRequestDto("Hello", notFoundChannelId, authorId, null);

        // 채널 조회 시 빈 값(Optional.empty) 반환하도록 설정
        given(channelRepository.findById(notFoundChannelId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ChannelNotFoundException.class, () -> basicMessageService.create(request, null));
    }

    @Test
    @DisplayName("존재하지 않는 작성자로 메시지를 생성하려고 하면 UserNotFoundException 예외가 발생한다.")
    void create_message_user_not_found_exception() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID notFoundAuthorId = UUID.randomUUID();
        CreateMessageRequestDto request = new CreateMessageRequestDto("Hello", channelId, notFoundAuthorId, null);

        // 채널은 정상 존재하지만 유저는 존재하지 않도록 설정
        Channel channel = new Channel(ChannelType.PUBLIC, "Test Channel", "Desc");
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(notFoundAuthorId)).willReturn(Optional.empty());

        // when & then
        assertThrows(com.sprint.mission.discodeit.exception.user.UserNotFoundException.class, () -> basicMessageService.create(request, null));
    }

    @Test
    @DisplayName("존재하지 않는 메시지 ID로 단일 조회 시 MessageNotFoundException 예외가 발생한다.")
    void find_message_not_found_exception() {
        // given
        UUID notFoundMessageId = UUID.randomUUID();
        given(messageRepository.findById(notFoundMessageId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MessageNotFoundException.class, () -> basicMessageService.find(notFoundMessageId));
    }
}