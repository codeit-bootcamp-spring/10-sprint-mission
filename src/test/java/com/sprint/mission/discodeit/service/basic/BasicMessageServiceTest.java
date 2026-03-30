package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.global.InvalidInputException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;
    @Mock
    private BinaryContentMapper binaryContentMapper;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService service;

    private User createUserWithStatus(UUID userId) {
        User user = new User("u1", "u1@test.com", "1234", null);
        UserStatus status = new UserStatus(user, Instant.now());

        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "userStatus", status);

        return user;
    }

    private Channel createChannel(UUID channelId) {
        Channel channel = new Channel("test", "description", ChannelType.PUBLIC);
        ReflectionTestUtils.setField(channel, "id", channelId);
        return channel;
    }

    private Message createMessage(UUID messageId, User user, Channel channel, String content, List<BinaryContent> attachments) {
        Message message = new Message(user, channel, content, attachments);
        ReflectionTestUtils.setField(message, "id", messageId);
        return message;
    }

    @Test
    @DisplayName("첨부파일 없이 메시지를 생성할 수 있습니다.")
    void create_message_without_attachments() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        User user = createUserWithStatus(authorId);
        Channel channel = createChannel(channelId);
        Message savedMessage = createMessage(messageId, user, channel, "content", List.of());

        UserDto userDto = new UserDto(authorId, "u1", "u1@test.com", null, null);
        MessageDto response = new MessageDto(messageId, null, null, "content", channelId, userDto, List.of());

        CreateMessageRequestDTO request =
                new CreateMessageRequestDTO("content", channelId, authorId);

        given(userRepository.findById(authorId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(messageRepository.saveAndFlush(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(savedMessage)).willReturn(response);

        // when
        MessageDto result = service.createMessage(request, List.of());

        // then
        assertThat(result).isEqualTo(response);

        then(userRepository).should().findById(authorId);
        then(channelRepository).should().findById(channelId);
        then(messageRepository).should().saveAndFlush(any(Message.class));
        then(messageMapper).should().toDto(savedMessage);

        then(binaryContentMapper).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("첨부파일과 함께 메시지를 생성할 수 있습니다.")
    void create_message_with_attachments() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();

        User user = createUserWithStatus(authorId);
        Channel channel = createChannel(channelId);

        byte[] bytes = "test-file".getBytes();
        long size = bytes.length;

        CreateBinaryContentPayloadDTO payload =
                new CreateBinaryContentPayloadDTO(bytes, "image/png", "file.png", size);

        BinaryContent attachment = new BinaryContent("image/png", "file.png", size);
        ReflectionTestUtils.setField(attachment, "id", attachmentId);

        Message savedMessage = createMessage(messageId, user, channel, "content", List.of(attachment));

        BinaryContentDto binaryContentDto =
                new BinaryContentDto(attachmentId, "file.png", size, "image/png");
        UserDto userDto = new UserDto(authorId, "u1", "u1@test.com", null, null);
        MessageDto response =
                new MessageDto(messageId, null, null, "content", channelId, userDto, List.of(binaryContentDto));

        CreateMessageRequestDTO request =
                new CreateMessageRequestDTO("content", channelId, authorId);

        given(userRepository.findById(authorId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(binaryContentMapper.toEntity(payload)).willReturn(attachment);
        given(messageRepository.saveAndFlush(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(savedMessage)).willReturn(response);

        // when
        MessageDto result = service.createMessage(request, List.of(payload));

        // then
        assertThat(result).isEqualTo(response);

        then(userRepository).should().findById(authorId);
        then(channelRepository).should().findById(channelId);
        then(binaryContentMapper).should().toEntity(payload);
        then(messageRepository).should().saveAndFlush(any(Message.class));
        then(binaryContentStorage).should().put(eq(attachmentId), eq(bytes));
        then(messageMapper).should().toDto(savedMessage);
    }

    @Test
    @DisplayName("존재하지 않는 작성자로 메시지를 생성하면 예외를 던집니다.")
    void create_message_fail_when_user_not_found() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        CreateMessageRequestDTO request =
                new CreateMessageRequestDTO("content", channelId, authorId);

        given(userRepository.findById(authorId)).willReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class,
                () -> service.createMessage(request, List.of()));

        then(userRepository).should().findById(authorId);
        then(channelRepository).should(never()).findById(any());
        then(messageRepository).should(never()).saveAndFlush(any());
        then(messageMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 채널에 메시지를 생성하면 예외를 던집니다.")
    void create_message_fail_when_channel_not_found() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        User user = createUserWithStatus(authorId);

        CreateMessageRequestDTO request =
                new CreateMessageRequestDTO("content", channelId, authorId);

        given(userRepository.findById(authorId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when, then
        assertThrows(ChannelNotFoundException.class,
                () -> service.createMessage(request, List.of()));

        then(userRepository).should().findById(authorId);
        then(channelRepository).should().findById(channelId);
        then(messageRepository).should(never()).saveAndFlush(any());
        then(messageMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("메시지 내용을 수정할 수 있습니다.")
    void update_message_success() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        User user = createUserWithStatus(authorId);
        Channel channel = createChannel(channelId);
        Message message = createMessage(messageId, user, channel, "old content", List.of());

        UpdateMessageRequestDTO request = new UpdateMessageRequestDTO("new content");

        UserDto userDto = new UserDto(authorId, "u1", "u1@test.com", null, null);
        MessageDto response =
                new MessageDto(messageId, null, null, "new content", channelId, userDto, List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(response);

        // when
        MessageDto result = service.updateMessage(messageId, request);

        // then
        assertThat(result).isEqualTo(response);
        assertThat(message.getContent()).isEqualTo("new content");

        then(messageRepository).should().findById(messageId);
        then(messageMapper).should().toDto(message);
    }

    @Test
    @DisplayName("수정할 메시지 내용이 null이면 예외를 던집니다.")
    void update_message_fail_when_content_is_null() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        User user = createUserWithStatus(authorId);
        Channel channel = createChannel(channelId);
        Message message = createMessage(messageId, user, channel, "old content", List.of());

        UpdateMessageRequestDTO request = new UpdateMessageRequestDTO(null);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when, then
        assertThrows(InvalidInputException.class,
                () -> service.updateMessage(messageId, request));

        then(messageRepository).should().findById(messageId);
        then(messageMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 메시지는 수정할 수 없습니다.")
    void update_message_fail_when_message_not_found() {
        // given
        UUID messageId = UUID.randomUUID();
        UpdateMessageRequestDTO request = new UpdateMessageRequestDTO("new content");

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when, then
        assertThrows(MessageNotFoundException.class,
                () -> service.updateMessage(messageId, request));

        then(messageRepository).should().findById(messageId);
        then(messageMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("cursor 없이 채널의 메시지 목록을 조회할 수 있습니다.")
    void find_all_by_channel_id_without_cursor() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        User user = createUserWithStatus(authorId);
        Channel channel = createChannel(channelId);
        Message message = createMessage(messageId, user, channel, "content", List.of());

        Pageable pageable = Pageable.ofSize(20);
        SliceImpl<Message> slice = new SliceImpl<>(List.of(message));

        UserDto userDto = new UserDto(authorId, "u1", "u1@test.com", null, null);
        MessageDto messageDto =
                new MessageDto(messageId, null, null, "content", channelId, userDto, List.of());

        PageResponse<MessageDto> response =
                new PageResponse<>(List.of(messageDto), null, 20, false, null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(messageRepository.findByChannel_IdOrderByCreatedAtDesc(eq(channelId), any(Pageable.class)))
                .willReturn(slice);
        given(messageMapper.toDtoList(List.of(message))).willReturn(List.of(messageDto));
        given(pageResponseMapper.fromSlice(eq(slice), eq(List.of(messageDto)), any()))
                .willReturn(response);

        // when
        PageResponse<MessageDto> result = service.findAllByChannelId(channelId, null, pageable);

        // then
        assertThat(result).isEqualTo(response);

        then(channelRepository).should().findById(channelId);
        then(messageRepository).should().findByChannel_IdOrderByCreatedAtDesc(eq(channelId), any(Pageable.class));
        then(messageMapper).should().toDtoList(List.of(message));
        then(pageResponseMapper).should().fromSlice(eq(slice), eq(List.of(messageDto)), any());
    }

    @Test
    @DisplayName("cursor와 함께 채널의 메시지 목록을 조회할 수 있습니다.")
    void find_all_by_channel_id_with_cursor() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        Instant cursor = Instant.now().minusSeconds(60);

        User user = createUserWithStatus(authorId);
        Channel channel = createChannel(channelId);
        Message message = createMessage(messageId, user, channel, "content", List.of());

        Pageable pageable = Pageable.ofSize(20);
        SliceImpl<Message> slice = new SliceImpl<>(List.of(message));

        UserDto userDto = new UserDto(authorId, "u1", "u1@test.com", null, null);
        MessageDto messageDto =
                new MessageDto(messageId, null, null, "content", channelId, userDto, List.of());

        PageResponse<MessageDto> response =
                new PageResponse<>(List.of(messageDto), null, 20, false, null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(messageRepository.findByChannelIdWithCursor(eq(channelId), eq(cursor), any(Pageable.class)))
                .willReturn(slice);
        given(messageMapper.toDtoList(List.of(message))).willReturn(List.of(messageDto));
        given(pageResponseMapper.fromSlice(eq(slice), eq(List.of(messageDto)), any()))
                .willReturn(response);

        // when
        PageResponse<MessageDto> result = service.findAllByChannelId(channelId, cursor, pageable);

        // then
        assertThat(result).isEqualTo(response);

        then(channelRepository).should().findById(channelId);
        then(messageRepository).should().findByChannelIdWithCursor(eq(channelId), eq(cursor), any(Pageable.class));
        then(messageMapper).should().toDtoList(List.of(message));
        then(pageResponseMapper).should().fromSlice(eq(slice), eq(List.of(messageDto)), any());
    }

    @Test
    @DisplayName("존재하지 않는 채널의 메시지 목록은 조회할 수 없습니다.")
    void find_all_by_channel_id_fail_when_channel_not_found() {
        // given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = Pageable.ofSize(20);

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when, then
        assertThrows(ChannelNotFoundException.class,
                () -> service.findAllByChannelId(channelId, null, pageable));

        then(channelRepository).should().findById(channelId);
        then(messageRepository).shouldHaveNoInteractions();
        then(messageMapper).shouldHaveNoInteractions();
        then(pageResponseMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하는 메시지는 삭제할 수 있습니다.")
    void delete_message_success() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        User user = createUserWithStatus(authorId);
        Channel channel = createChannel(channelId);
        Message message = createMessage(messageId, user, channel, "content", List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        service.deleteMessage(messageId);

        // then
        then(messageRepository).should().findById(messageId);
        then(messageRepository).should().deleteById(messageId);
    }

    @Test
    @DisplayName("존재하지 않는 메시지는 삭제할 수 없습니다.")
    void delete_message_fail_when_message_not_found() {
        // given
        UUID messageId = UUID.randomUUID();

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when, then
        assertThrows(MessageNotFoundException.class,
                () -> service.deleteMessage(messageId));

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should(never()).deleteById(any());
    }
}