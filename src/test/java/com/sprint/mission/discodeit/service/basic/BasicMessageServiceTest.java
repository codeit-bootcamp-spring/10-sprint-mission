package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
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
  private MessageMapper messageMapper;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;

  private UUID messageId;
  private UUID channelId;
  private UUID authorId;
  private String content;
  private Message message;
  private MessageDto messageDto;
  private Channel channel;
  private User author;
  private BinaryContent attachment;
  private BinaryContentDto attachmentDto;

  @BeforeEach
  void setUp() {
    messageId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    authorId = UUID.randomUUID();
    content = "test message";

    channel = new Channel(ChannelType.PUBLIC, "testChannel", "testDescription");
    ReflectionTestUtils.setField(channel, "id", channelId);

    author = new User("testUser", "test@example.com", "password", null, UserRole.USER);
    ReflectionTestUtils.setField(author, "id", authorId);

    attachment = new BinaryContent("test.txt", 100L, "text/plain");
    ReflectionTestUtils.setField(attachment, "id", UUID.randomUUID());
    attachmentDto = new BinaryContentDto(attachment.getId(), "test.txt", 100L, "text/plain");

    message = new Message(content, channel, author, List.of(attachment));
    ReflectionTestUtils.setField(message, "id", messageId);

    messageDto = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        content,
        channelId,
        new UserDto(authorId, "testUser", "test@example.com", null, true, UserRole.USER),
        List.of(attachmentDto)
    );
  }

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessage_Success() {

    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest("test.txt",
        "text/plain", new byte[100]);
    List<BinaryContentCreateRequest> attachmentRequests = List.of(attachmentRequest);

    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(channel));
    given(userRepository.findById(eq(authorId))).willReturn(Optional.of(author));
    given(binaryContentRepository.save(any(BinaryContent.class))).will(invocation -> {
      BinaryContent binaryContent = invocation.getArgument(0);
      ReflectionTestUtils.setField(binaryContent, "id", attachment.getId());
      return attachment;
    });
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

    MessageDto result = messageService.create(request, attachmentRequests);

    assertThat(result).isEqualTo(messageDto);
    verify(messageRepository).save(any(Message.class));
    verify(binaryContentStorage).put(eq(attachment.getId()), any(byte[].class));
  }

  @Test
  @DisplayName("존재하지 않는 채널에 메시지 생성 시도 시 실패")
  void createMessage_WithNonExistentChannel_ThrowsException() {

    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("존재하지 않는 작성자로 메시지 생성 시도 시 실패")
  void createMessage_WithNonExistentAuthor_ThrowsException() {

    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(channel));
    given(userRepository.findById(eq(authorId))).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 조회 성공")
  void findMessage_Success() {

    given(messageRepository.findById(eq(messageId))).willReturn(Optional.of(message));
    given(messageMapper.toDto(eq(message))).willReturn(messageDto);

    MessageDto result = messageService.find(messageId);

    assertThat(result).isEqualTo(messageDto);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 조회 시 실패")
  void findMessage_WithNonExistentId_ThrowsException() {

    given(messageRepository.findById(eq(messageId))).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.find(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 성공")
  void findAllByChannelId_Success() {

    int pageSize = 2;
    Instant createdAt = Instant.now();
    Pageable pageable = PageRequest.of(0, pageSize);

    Message message1 = new Message(content + "1", channel, author, List.of(attachment));
    Message message2 = new Message(content + "2", channel, author, List.of(attachment));
    Message message3 = new Message(content + "3", channel, author, List.of(attachment));

    ReflectionTestUtils.setField(message1, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(message2, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(message3, "id", UUID.randomUUID());

    Instant message1CreatedAt = Instant.now().minusSeconds(30);
    Instant message2CreatedAt = Instant.now().minusSeconds(20);
    Instant message3CreatedAt = Instant.now().minusSeconds(10);

    ReflectionTestUtils.setField(message1, "createdAt", message1CreatedAt);
    ReflectionTestUtils.setField(message2, "createdAt", message2CreatedAt);
    ReflectionTestUtils.setField(message3, "createdAt", message3CreatedAt);

    MessageDto messageDto1 = new MessageDto(
        message1.getId(),
        message1CreatedAt,
        message1CreatedAt,
        content + "1",
        channelId,
        new UserDto(authorId, "testUser", "test@example.com", null, true, UserRole.USER),
        List.of(attachmentDto)
    );

    MessageDto messageDto2 = new MessageDto(
        message2.getId(),
        message2CreatedAt,
        message2CreatedAt,
        content + "2",
        channelId,
        new UserDto(authorId, "testUser", "test@example.com", null, true, UserRole.USER),
        List.of(attachmentDto)
    );

    List<Message> firstPageMessages = List.of(message1, message2);
    List<MessageDto> firstPageDtos = List.of(messageDto1, messageDto2);

    SliceImpl<Message> firstPageSlice = new SliceImpl<>(firstPageMessages, pageable, true);
    PageResponse<MessageDto> firstPageResponse = new PageResponse<>(
        firstPageDtos,
        message2CreatedAt,
        pageSize,
        true,
        null
    );

    given(
        messageRepository.findAllByChannelIdWithAuthor(eq(channelId), eq(createdAt), eq(pageable)))
        .willReturn(firstPageSlice);
    given(messageMapper.toDto(eq(message1))).willReturn(messageDto1);
    given(messageMapper.toDto(eq(message2))).willReturn(messageDto2);
    given(pageResponseMapper.<MessageDto>fromSlice(any(), eq(message2CreatedAt)))
        .willReturn(firstPageResponse);

    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, createdAt,
        pageable);

    assertThat(result).isEqualTo(firstPageResponse);
    assertThat(result.content()).hasSize(pageSize);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.nextCursor()).isEqualTo(message2CreatedAt);

    List<Message> secondPageMessages = List.of(message3);
    MessageDto messageDto3 = new MessageDto(
        message3.getId(),
        message3CreatedAt,
        message3CreatedAt,
        content + "3",
        channelId,
        new UserDto(authorId, "testUser", "test@example.com", null, true, UserRole.USER),
        List.of(attachmentDto)
    );
    List<MessageDto> secondPageDtos = List.of(messageDto3);

    SliceImpl<Message> secondPageSlice = new SliceImpl<>(secondPageMessages, pageable, false);
    PageResponse<MessageDto> secondPageResponse = new PageResponse<>(
        secondPageDtos,
        message3CreatedAt,
        pageSize,
        false,
        null
    );

    given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), eq(message2CreatedAt),
        eq(pageable)))
        .willReturn(secondPageSlice);
    given(messageMapper.toDto(eq(message3))).willReturn(messageDto3);
    given(pageResponseMapper.<MessageDto>fromSlice(any(), eq(message3CreatedAt)))
        .willReturn(secondPageResponse);

    PageResponse<MessageDto> secondResult = messageService.findAllByChannelId(channelId,
        message2CreatedAt,
        pageable);

    assertThat(secondResult).isEqualTo(secondPageResponse);
    assertThat(secondResult.content()).hasSize(1);
    assertThat(secondResult.hasNext()).isFalse();
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void updateMessage_Success() {

    String newContent = "updated content";
    MessageUpdateRequest request = new MessageUpdateRequest(newContent);

    given(messageRepository.findById(eq(messageId))).willReturn(Optional.of(message));
    given(messageMapper.toDto(eq(message))).willReturn(messageDto);

    MessageDto result = messageService.update(messageId, request);

    assertThat(result).isEqualTo(messageDto);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 시도 시 실패")
  void updateMessage_WithNonExistentId_ThrowsException() {

    MessageUpdateRequest request = new MessageUpdateRequest("new content");
    given(messageRepository.findById(eq(messageId))).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_Success() {

    given(messageRepository.existsById(eq(messageId))).willReturn(true);

    messageService.delete(messageId);

    verify(messageRepository).deleteById(eq(messageId));
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 시도 시 실패")
  void deleteMessage_WithNonExistentId_ThrowsException() {

    given(messageRepository.existsById(eq(messageId))).willReturn(false);

    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }
}
