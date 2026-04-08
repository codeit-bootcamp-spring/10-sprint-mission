package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private BinaryContentService binaryContentService;

    @Mock
    private MessageMapper messageMapper;

    private final PageResponseMapper pageResponseMapper = new PageResponseMapper() {
    };

    private BasicMessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new BasicMessageService(
                userRepository,
                channelRepository,
                messageRepository,
                binaryContentService,
                messageMapper,
                pageResponseMapper
        );
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage_success() {
        UUID requesterId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        User user = new User("dustle", "1234", "dustle@test.com");
        Channel channel = Channel.buildPublic("general", "desc");
        setId(user, requesterId);
        setId(channel, channelId);

        Message savedMessage = new Message(user, channel, "hello");
        setId(savedMessage, messageId);
        setCreatedAt(savedMessage, Instant.parse("2026-04-01T00:00:00Z"));

        MessageCreateRequest request = new MessageCreateRequest("hello", channelId, requesterId);
        MessageDto expected = new MessageDto(messageId, savedMessage.getCreatedAt(), null, "hello", channelId, null, List.of());

        given(userRepository.findById(requesterId)).willReturn(java.util.Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(java.util.Optional.of(channel));
        given(binaryContentService.createBinaryContents(List.of())).willReturn(List.of());
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(savedMessage)).willReturn(expected);

        MessageDto result = messageService.createMessage(requesterId, request, List.of());

        assertThat(result).isEqualTo(expected);
        assertThat(channel.getLastMessageAt()).isEqualTo(savedMessage.getCreatedAt());
        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 메시지 생성 실패")
    void createMessage_fail_whenUserNotFound() {
        UUID requesterId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("hello", channelId, requesterId);
        given(userRepository.findById(requesterId)).willReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> messageService.createMessage(requesterId, request, List.of()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessage_success() {
        UUID messageId = UUID.randomUUID();
        User user = new User("dustle", "1234", "dustle@test.com");
        Channel channel = Channel.buildPublic("general", "desc");
        Message message = new Message(user, channel, "before");
        setId(message, messageId);

        MessageDto expected = new MessageDto(messageId, null, null, "after", null, null, List.of());
        given(messageRepository.findById(messageId)).willReturn(java.util.Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(expected);

        MessageDto result = messageService.updateMessage(messageId, new MessageUpdateRequest("after"));

        assertThat(result).isEqualTo(expected);
        assertThat(message.getContent()).isEqualTo("after");
    }

    @Test
    @DisplayName("존재하지 않는 메시지 수정 실패")
    void updateMessage_fail_whenMessageNotFound() {
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> messageService.updateMessage(messageId, new MessageUpdateRequest("after")))
                .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessage_success() {
        UUID messageId = UUID.randomUUID();
        User user = new User("dustle", "1234", "dustle@test.com");
        Channel channel = Channel.buildPublic("general", "desc");
        Message message = new Message(user, channel, "hello");
        setId(message, messageId);

        given(messageRepository.findById(messageId)).willReturn(java.util.Optional.of(message));

        messageService.deleteMessage(messageId);

        then(messageRepository).should().delete(message);
        assertThat(message.getAuthor()).isNull();
        assertThat(message.getChannel()).isNull();
        assertThat(message.getAttachments()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 실패")
    void deleteMessage_fail_whenMessageNotFound() {
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> messageService.deleteMessage(messageId))
                .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("메시지 커서 페이지네이션 조회 성공")
    void findAllMessagesByChannelId_success() {
        UUID channelId = UUID.randomUUID();
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();

        User user = new User("dustle", "1234", "dustle@test.com");
        Channel channel = Channel.buildPublic("general", "desc");
        setId(channel, channelId);

        Message first = new Message(user, channel, "first");
        Message second = new Message(user, channel, "second");
        setId(first, firstId);
        setId(second, secondId);

        Slice<Message> messageSlice = new SliceImpl<>(
                List.of(first, second),
                PageRequest.of(0, 2),
                true
        );
        MessageDto firstDto = new MessageDto(firstId, null, null, "first", channelId, null, List.of());
        MessageDto secondDto = new MessageDto(secondId, null, null, "second", channelId, null, List.of());

        given(messageRepository.findByChannel_IdOrderByCreatedAtDescIdDesc(eq(channelId), any()))
                .willReturn(messageSlice);
        given(messageMapper.toDto(first)).willReturn(firstDto);
        given(messageMapper.toDto(second)).willReturn(secondDto);

        PageResponse<MessageDto> result = messageService.findAllMessagesByChannelId(channelId, null, PageRequest.of(0, 2));

        assertThat(result.content()).containsExactly(firstDto, secondDto);
        assertThat(result.nextCursor()).isEqualTo(secondId);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("다른 채널의 커서 메시지로 조회 실패")
    void findAllMessagesByChannelId_fail_whenCursorBelongsToAnotherChannel() {
        UUID requestedChannelId = UUID.randomUUID();
        UUID cursorId = UUID.randomUUID();
        Channel anotherChannel = Channel.buildPublic("another", "desc");
        setId(anotherChannel, UUID.randomUUID());
        User user = new User("dustle", "1234", "dustle@test.com");
        Message cursorMessage = new Message(user, anotherChannel, "cursor");
        setId(cursorMessage, cursorId);

        given(messageRepository.findById(cursorId)).willReturn(java.util.Optional.of(cursorMessage));

        assertThatThrownBy(() -> messageService.findAllMessagesByChannelId(
                requestedChannelId,
                cursorId,
                PageRequest.of(0, 20)
        )).isInstanceOf(MessageNotFoundException.class);
    }

    private static void setId(Object target, UUID id) {
        ReflectionTestUtils.setField(target, "id", id);
    }

    private static void setCreatedAt(Object target, Instant createdAt) {
        ReflectionTestUtils.setField(target, "createdAt", createdAt);
    }
}
