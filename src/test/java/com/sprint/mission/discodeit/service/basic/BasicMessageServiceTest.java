package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
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
  private BinaryContentStorage binaryContentsStorage;
  @Mock
  private BinaryContentMapper binaryContentMapper;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;


  @Test
  @DisplayName("성공: 바이너리 컨텐츠가 있는 메세지 생성 성공")
  void createMessageWithBinaryContentsSuccess() {
    //given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    Channel channel = mock(Channel.class);
    User author = mock(User.class);
    MessageCreateRequest dto = new MessageCreateRequest("Hello World", channelId, authorId);
    List<BinaryContentCreateDto> attachments = List.of(mock(BinaryContentCreateDto.class),
        mock(BinaryContentCreateDto.class));
    Message message = Message.create(dto.content(), channel, author,
        List.of(mock(BinaryContent.class), mock(BinaryContent.class)));

    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(authorId, channelId)).willReturn(
        Optional.of(mock(ReadStatus.class)));
    given(binaryContentMapper.toEntity(any(BinaryContentCreateDto.class))).willReturn(
        mock(BinaryContent.class));
    given(messageMapper.toEntity(eq(dto), eq(author), eq(channel), anyList())).willReturn(message);
    given(messageMapper.toDto(message)).willReturn(mock(MessageDto.class));

    //when
    MessageDto result = messageService.create(dto, attachments);

    //then
    assertNotNull(result);
    // 메세지 저장 검증
    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    then(messageRepository).should().save(messageCaptor.capture());
    Message capturedMessage = messageCaptor.getValue();
    assertEquals(dto.content(), capturedMessage.getContent());
    assertEquals(channel, capturedMessage.getChannel());
    assertEquals(author, capturedMessage.getAuthor());
    assertEquals(attachments.size(), capturedMessage.getAttachments().size());
  }

  @Test
  @DisplayName("실패: 멤버가 아닌 채널에 대한 메세지 생성 실패")
  void createMessageToNotIncludedChannelFailure() {
    //given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    Channel channel = mock(Channel.class);
    User author = mock(User.class);
    MessageCreateRequest messageCreateRequest = new MessageCreateRequest("test", channelId,
        authorId);

    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(authorId, channelId)).willReturn(
        Optional.empty());

    //when & then
    assertThrows(ReadStatusNotFoundException.class,
        () -> messageService.create(messageCreateRequest, List.of()));
  }

  @Test
  @DisplayName("성공: 채널 아이디와 커서 페이지네이션 정보로 메세지 목록 조회 성공")
  void findAllByChannelIdWithCursorAndPagingInfoSuccess() {
    //given
    UUID channelId = UUID.randomUUID();
    Channel channel = mock(Channel.class);
    Pageable pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");
    Instant cursor = Instant.now();

    // 메세지 목록
    BinaryContent attachment1 = BinaryContent.create("file1", "jpg", 50);
    BinaryContent attachment2 = BinaryContent.create("file2", "tet", 100);
    Message m1 = Message.create("1st", channel, mock(User.class),
        List.of(attachment1, attachment2));
    Message m2 = Message.create("2nd", channel, mock(User.class), null);
    ReflectionTestUtils.setField(m1, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(m1, "createdAt", cursor.minusSeconds(10));
    ReflectionTestUtils.setField(m2, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(m2, "createdAt", cursor.minusSeconds(20));

    Slice<Message> messageSlice = new SliceImpl<>(List.of(m1, m2), pageable, true); // 다음 페이지 있음
    MessageDto d1 = mock(MessageDto.class);
    MessageDto d2 = mock(MessageDto.class);
    Slice<MessageDto> dtoSlice = new SliceImpl<>(List.of(d1, d2), pageable, true);

    given(
        messageRepository.findAllByChannelIdFetchUserInfo(channelId, pageable, cursor)).willReturn(
        messageSlice);
    given(messageMapper.toDto(eq(m1), any())).willReturn(d1);
    given(messageMapper.toDto(eq(m2), any())).willReturn(d2);
    given(pageResponseMapper.fromSlice(any(Slice.class), any(Instant.class))).willReturn(
        new PageResponse<>(dtoSlice.getContent(), m2.getCreatedAt(),
            dtoSlice.getSize(), dtoSlice.hasNext(), null));

    //when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, pageable,
        cursor);

    //then
    assertNotNull(result);
    assertEquals(messageSlice.getContent().size(), result.content().size());
    assertTrue(result.hasNext());
    assertEquals(m2.getCreatedAt(), result.nextCursor()); // 마지막 메시지의 시간이 커서
  }

  @Test
  @DisplayName("성공: 메시지가 하나도 없는 채널 조회 시 빈 결과 반환 성공")
  void findAllByChannelIdEmpty() {
    // given
    UUID channelId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
    Instant cursor = Instant.now();
    Slice<Message> emptySlice = new SliceImpl<>(List.of(), pageable, false);

    given(messageRepository.findAllByChannelIdFetchUserInfo(any(), any(), any()))
        .willReturn(emptySlice);
    given(pageResponseMapper.fromSlice(any(), any()))
        .willReturn(new PageResponse<>(List.of(), null, 0, false, null));

    // when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, pageable,
        cursor);

    // then
    assertTrue(result.content().isEmpty());
    assertFalse(result.hasNext());
    assertNull(result.nextCursor());

    // 메시지가 없으므로 첨부파일 조회 쿼리 없음
    then(messageRepository).should(never()).findAllByIdInFetchAttachments(anyList());
  }

  @Test
  @DisplayName("성공: 메세지 내용 수정 성공")
  void updateMessageSuccess() {
    //given
    UUID messageId = UUID.randomUUID();
    Channel channel = mock(Channel.class);
    User author = mock(User.class);
    Message message = Message.create("Old Content", channel, author, null);
    MessageUpdateRequest updateDto = new MessageUpdateRequest("New Content");

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toDto(message)).willReturn(mock(MessageDto.class));

    //when
    MessageDto result = messageService.update(messageId, updateDto);

    //then
    assertNotNull(result);
    assertEquals(updateDto.newContent(), message.getContent());
  }

  @Test
  @DisplayName("실패: 유효하지 않은 메세지 수정 실패")
  void updateInvalidMessageFailure() {
    //given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest updateDto = new MessageUpdateRequest("New Content");

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    //when & then
    assertThrows(MessageNotFoundException.class, () ->
        messageService.update(messageId, updateDto)
    );
  }

  @Test
  @DisplayName("성공: 첨부파일이 있는 메세지 삭제 성공")
  void deleteMessageWithAttachmentsSuccess() {
    //given
    Channel channel = mock(Channel.class);
    User author = mock(User.class);
    UUID messageId = UUID.randomUUID();

    Message message = Message.create("Content", channel, author,
        List.of(mock(BinaryContent.class)));

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    //when
    messageService.delete(messageId);

    //then
    then(messageRepository).should(times(1)).deleteById(messageId);
  }

  @Test
  @DisplayName("실패: 유효하지 않은 메세지 삭제 실패")
  void deleteInvalidMessageFailure() {
    //given
    Channel channel = mock(Channel.class);
    User author = mock(User.class);
    UUID messageId = UUID.randomUUID();

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    //when & then
    assertThrows(MessageNotFoundException.class, () -> messageService.delete(messageId));
  }
}