package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyBoolean;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageEmptyException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageDtoMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentService binaryContentService;

  @Mock
  private MessageDtoMapper messageDtoMapper;

  @Mock
  private BinaryContentMapper binaryContentMapper;

  @Mock
  private UserMapper userMapper;

  @Mock
  private JwtRegistry jwtRegistry;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("메시지는 정상적으로 생성되어야 합니다.")
  void create_success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
    ReflectionTestUtils.setField(channel, "id", channelId);

    User author = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(author, "id", authorId);

    Message message = new Message(channel, author, "hello", List.of());
    ReflectionTestUtils.setField(message, "id", messageId);

    UserDto authorDto = new UserDto(
        authorId,
        author.getUsername(),
        author.getEmail(),
        null,
        false,
        UserRole.USER
    );

    MessageDto response = new MessageDto(
        messageId,
        message.getCreatedAt(),
        message.getUpdatedAt(),
        "hello",
        channelId,
        authorDto,
        List.of()
    );

    MessageCreateRequest request =
        new MessageCreateRequest(channelId, authorId, "hello", List.of());

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(jwtRegistry.hasActiveJwtInformationByUserId(any(UUID.class))).willReturn(false);
    given(userMapper.toDto(any(User.class), anyBoolean(), any())).willReturn(authorDto);
    given(messageDtoMapper.toDto(any(Message.class), any(), anyList())).willReturn(response);

    // when
    MessageDto result = messageService.create(request);

    // then
    assertThat(result.id()).isEqualTo(messageId);
    assertThat(result.content()).isEqualTo("hello");
    assertThat(result.channelId()).isEqualTo(channelId);

    then(channelRepository).should().findById(channelId);
    then(userRepository).should().findById(authorId);
    then(messageRepository).should().save(any(Message.class));
    then(jwtRegistry).should().hasActiveJwtInformationByUserId(authorId);
  }

  @Test
  @DisplayName("존재하지 않는 채널에 메시지 생성 시 예외가 발생해야 합니다.")
  void create_fail_channel_not_found() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageCreateRequest request =
        new MessageCreateRequest(channelId, authorId, "hello", List.of());

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> messageService.create(request))
        .isInstanceOf(ChannelNotFoundException.class);

    then(userRepository).should(never()).findById(authorId);
    then(messageRepository).should(never()).save(any(Message.class));
  }

  @Test
  @DisplayName("존재하지 않는 작성자로 메시지 생성 시 예외가 발생해야 합니다.")
  void create_fail_user_not_found() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
    ReflectionTestUtils.setField(channel, "id", channelId);

    MessageCreateRequest request =
        new MessageCreateRequest(channelId, authorId, "hello", List.of());

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> messageService.create(request))
        .isInstanceOf(UserNotFoundException.class);

    then(messageRepository).should(never()).save(any(Message.class));
  }

  @Test
  @DisplayName("빈 내용으로 메시지 생성 시 예외가 발생해야 합니다.")
  void create_fail_empty_content() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageCreateRequest request =
        new MessageCreateRequest(channelId, authorId, "   ", List.of());

    // when, then
    assertThatThrownBy(() -> messageService.create(request))
        .isInstanceOf(MessageEmptyException.class);

    then(channelRepository).should(never()).findById(channelId);
  }

  @Test
  @DisplayName("메시지 내용은 정상적으로 수정되어야 합니다.")
  void update_success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
    ReflectionTestUtils.setField(channel, "id", channelId);

    User author = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(author, "id", authorId);

    Message message = new Message(channel, author, "before", List.of());
    ReflectionTestUtils.setField(message, "id", messageId);

    UserDto authorDto = new UserDto(
        authorId,
        author.getUsername(),
        author.getEmail(),
        null,
        false,
        UserRole.USER
    );

    MessageDto response = new MessageDto(
        messageId,
        message.getCreatedAt(),
        message.getUpdatedAt(),
        "after",
        channelId,
        authorDto,
        List.of()
    );

    MessageUpdateRequest request = new MessageUpdateRequest(messageId, "after");

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(jwtRegistry.hasActiveJwtInformationByUserId(any(UUID.class))).willReturn(false);
    given(userMapper.toDto(any(User.class), anyBoolean(), any())).willReturn(authorDto);
    given(messageDtoMapper.toDto(any(Message.class), any(), anyList())).willReturn(response);

    // when
    MessageDto result = messageService.update(request);

    // then
    assertThat(result.id()).isEqualTo(messageId);
    assertThat(result.content()).isEqualTo("after");

    then(messageRepository).should().findById(messageId);
    then(jwtRegistry).should().hasActiveJwtInformationByUserId(authorId);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 시 예외가 발생해야 합니다.")
  void update_fail_not_found() {
    // given
    UUID messageId = UUID.randomUUID();

    MessageUpdateRequest request = new MessageUpdateRequest(messageId, "after");

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> messageService.update(request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("빈 내용으로 메시지 수정 시 예외가 발생해야 합니다.")
  void update_fail_empty_content() {
    // given
    UUID messageId = UUID.randomUUID();

    MessageUpdateRequest request = new MessageUpdateRequest(messageId, " ");

    // when, then
    assertThatThrownBy(() -> messageService.update(request))
        .isInstanceOf(MessageEmptyException.class);

    then(messageRepository).should(never()).findById(messageId);
  }

  @Test
  @DisplayName("메시지는 정상적으로 삭제되어야 합니다.")
  void delete_success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
    ReflectionTestUtils.setField(channel, "id", channelId);

    User author = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(author, "id", authorId);

    Message message = new Message(channel, author, "hello", List.of());
    ReflectionTestUtils.setField(message, "id", messageId);

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    // when
    messageService.delete(messageId);

    // then
    then(messageRepository).should().findById(messageId);
    then(messageRepository).should().delete(message);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 시 예외가 발생해야 합니다.")
  void delete_fail_not_found() {
    // given
    UUID messageId = UUID.randomUUID();

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);

    then(messageRepository).should(never()).delete(any(Message.class));
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록을 정상적으로 조회해야 합니다.")
  void findAllByChannelId_success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();

    Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc");
    ReflectionTestUtils.setField(channel, "id", channelId);

    User author = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(author, "id", authorId);

    Message message = new Message(channel, author, "hello", List.of());
    ReflectionTestUtils.setField(message, "id", messageId);

    UserDto authorDto = new UserDto(
        authorId,
        author.getUsername(),
        author.getEmail(),
        null,
        false,
        UserRole.USER
    );

    MessageDto messageDto = new MessageDto(
        messageId,
        message.getCreatedAt(),
        message.getUpdatedAt(),
        "hello",
        channelId,
        authorDto,
        List.of()
    );

    given(channelRepository.findChannel(channelId)).willReturn(channel);
    given(messageRepository.findMessageIdsByChannelId(any(UUID.class), any(Pageable.class)))
        .willReturn(List.of(messageId));
    given(messageRepository.countByChannel_Id(channelId)).willReturn(1L);
    given(messageRepository.findAllByIdInWithAuthorAndAttachments(List.of(messageId)))
        .willReturn(List.of(message));
    given(userRepository.findAllByIdInWithProfileImage(List.of(authorId)))
        .willReturn(List.of(author));
    given(jwtRegistry.hasActiveJwtInformationByUserId(any(UUID.class))).willReturn(false);
    given(userMapper.toDto(any(User.class), anyBoolean(), any())).willReturn(authorDto);
    given(messageDtoMapper.toDto(any(Message.class), any(), anyList())).willReturn(messageDto);

    // when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, 50);

    // then
    assertThat(result.content()).hasSize(1);
    assertThat(result.content().get(0).id()).isEqualTo(messageId);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.totalElements()).isEqualTo(1L);

    then(channelRepository).should().findChannel(channelId);
    then(messageRepository).should()
        .findMessageIdsByChannelId(any(UUID.class), any(Pageable.class));
    then(messageRepository).should().findAllByIdInWithAuthorAndAttachments(List.of(messageId));
    then(jwtRegistry).should().hasActiveJwtInformationByUserId(authorId);
  }

  @Test
  @DisplayName("존재하지 않는 채널의 메시지 목록 조회 시 예외가 발생해야 합니다.")
  void findAllByChannelId_fail_channel_not_found() {
    // given
    UUID channelId = UUID.randomUUID();

    given(channelRepository.findChannel(channelId)).willReturn(null);

    // when, then
    assertThatThrownBy(() -> messageService.findAllByChannelId(channelId, null, 50))
        .isInstanceOf(ChannelNotFoundException.class);

    then(messageRepository).should(never()).findMessageIdsByChannelId(any(), any());
  }
}