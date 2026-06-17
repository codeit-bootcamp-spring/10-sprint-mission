package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelParticipantException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private MessageMapper messageMapper;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private PageMapper pageMapper;

  @Mock
  ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private BasicMessageService basicMessageService;

  @Test
  @DisplayName("첨부파일이 있는 메시지 생성에 성공해야 한다.")
  void should_create_message_when_attachment_is_present() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("메시지 생성 테스트", fixedUuid, fixedUuid);

    byte[] fakeImageBytes = "가짜 이미지 데이터".getBytes();
    MultipartFile multipartFile = new MockMultipartFile("file", "attachment.png", "image/png",
        fakeImageBytes);
    List<MultipartFile> multipartFiles = List.of(multipartFile, multipartFile);
    BinaryContent expectedAttachment = new BinaryContent("attachment.png", fakeImageBytes.length,
        "image/png");

    Channel fixedChannel = new Channel(ChannelType.PUBLIC, "일반 채널", "일반 채널 입니다.");
    User fixedUser = new User("김코딩", "hello@hello.com", "1234", null);
    Message expectedMessage = new Message("메시지 생성 테스트", fixedChannel, fixedUser);

    given(userRepository.findById(any())).willReturn(Optional.of(fixedUser));
    given(channelRepository.findById(any())).willReturn(Optional.of(fixedChannel));
    given(messageMapper.toEntity(request, fixedChannel, fixedUser)).willReturn(expectedMessage);
    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(expectedAttachment);
    given(messageRepository.save(any(Message.class))).willReturn(expectedMessage);
    // when
    basicMessageService.create(request, multipartFiles);
    // then
    then(userRepository).should(times(1)).findById(any(UUID.class));
    then(channelRepository).should(times(1)).findById(any(UUID.class));
    then(binaryContentRepository).should(times(multipartFiles.size()))
        .save(any(BinaryContent.class));
    then(messageRepository).should(times(1)).save(expectedMessage);
  }

  @Test
  @DisplayName("첨부파일이 없는 메시지 생성에 성공해야 한다.")
  void should_create_message_when_attachment_is_null() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("메시지 생성 테스트", fixedUuid, fixedUuid);

    Channel fixedChannel = new Channel(ChannelType.PUBLIC, "일반 채널", "일반 채널 입니다.");
    User fixedUser = new User("김코딩", "hello@hello.com", "1234", null);
    Message expectedMessage = new Message("메시지 생성 테스트", fixedChannel, fixedUser);

    given(userRepository.findById(any())).willReturn(Optional.of(fixedUser));
    given(channelRepository.findById(any())).willReturn(Optional.of(fixedChannel));
    given(messageMapper.toEntity(request, fixedChannel, fixedUser)).willReturn(expectedMessage);
    given(messageRepository.save(any(Message.class))).willReturn(expectedMessage);
    // when
    basicMessageService.create(request, null);
    // then
    then(userRepository).should(times(1)).findById(any(UUID.class));
    then(channelRepository).should(times(1)).findById(any(UUID.class));
    then(messageRepository).should(times(1)).save(expectedMessage);
  }

  @Test
  @DisplayName("PRIVATE 채널에 비참여자가 글을 쓰려고 하면 예외를 던져야 한다.")
  void should_fail_create_message_when_non_participant_sends_to_private_channel() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("메시지 생성 테스트", fixedUuid, fixedUuid);

    Channel fixedChannel = new Channel(ChannelType.PRIVATE, null, null);
    User fixedUser = new User("김코딩", "hello@hello.com", "1234", null);

    given(userRepository.findById(any())).willReturn(Optional.of(fixedUser));
    given(channelRepository.findById(any())).willReturn(Optional.of(fixedChannel));
    given(
        readStatusRepository.findByUserIdAndChannelId(any(UUID.class), any(UUID.class))).willReturn(
        Optional.empty());
    // when, then
    assertThrows(ChannelParticipantException.class, () -> {
      basicMessageService.create(request, null);
    });
    then(userRepository).should(times(1)).findById(any(UUID.class));
    then(channelRepository).should(times(1)).findById(any(UUID.class));
    then(messageRepository).should(never()).save(any(Message.class));
  }

  @Test
  @DisplayName("채널 Id로 메시지 목록 조회에 성공해야 한다.")
  void should_find_all_message_by_channel_id() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    UserDto fixedUserDto = new UserDto(fixedUuid, "가짜 유저", "fake@email.com", null, true, Role.USER);
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 2);
    Instant msg1Time = cursor.minusSeconds(10);
    Instant msg2Time = cursor.minusSeconds(20);
    Message msg1 = new Message("첫 번째 메시지", null, null);
    Message msg2 = new Message("두 번째 메시지", null, null);
    List<Message> messages = List.of(msg1, msg2);
    Slice<Message> messageSlice = new SliceImpl<>(messages, pageable, true);
    MessageDto messageDto1 = new MessageDto(fixedUuid, msg1Time, msg1Time, "첫 번째 메시지", fixedUuid,
        fixedUserDto, null);
    MessageDto messageDto2 = new MessageDto(fixedUuid, msg2Time, msg2Time, "두 번째 메시지", fixedUuid,
        fixedUserDto, null);

    given(channelRepository.existsById(fixedUuid)).willReturn(true);
    given(messageRepository.findAllByChannelIdAndCreatedAtLessThan(any(UUID.class),
        any(Instant.class),
        any(Pageable.class)))
        .willReturn(messageSlice);
    given(messageMapper.toDto(msg1)).willReturn(messageDto1);
    given(messageMapper.toDto(msg2)).willReturn(messageDto2);
    // when
    basicMessageService.findAllByChannelId(fixedUuid, Instant.now(), pageable);
    // then
    then(pageMapper).should(times(1)).fromSlice(any(Slice.class), eq(msg2Time));
  }

  @Test
  @DisplayName("마지막 페이지에 도달하면 다음 커서는 null이 되어야 한다.")
  void should_return_null_next_cursor_when_last_page() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    UserDto fixedUserDto = new UserDto(fixedUuid, "가짜 유저", "fake@email.com", null, true, Role.USER);
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 2);
    Instant msg1Time = cursor.minusSeconds(10);
    Instant msg2Time = cursor.minusSeconds(20);
    Message msg1 = new Message("첫 번째 메시지", null, null);
    Message msg2 = new Message("두 번째 메시지", null, null);
    List<Message> messages = List.of(msg1, msg2);
    Slice<Message> messageSlice = new SliceImpl<>(messages, pageable, false);
    MessageDto messageDto1 = new MessageDto(fixedUuid, msg1Time, msg1Time, "첫 번째 메시지", fixedUuid,
        fixedUserDto, null);
    MessageDto messageDto2 = new MessageDto(fixedUuid, msg2Time, msg2Time, "두 번째 메시지", fixedUuid,
        fixedUserDto, null);

    given(channelRepository.existsById(fixedUuid)).willReturn(true);
    given(messageRepository.findAllByChannelIdAndCreatedAtLessThan(any(UUID.class),
        any(Instant.class),
        any(Pageable.class)))
        .willReturn(messageSlice);
    given(messageMapper.toDto(msg1)).willReturn(messageDto1);
    given(messageMapper.toDto(msg2)).willReturn(messageDto2);
    // when
    basicMessageService.findAllByChannelId(fixedUuid, Instant.now(), pageable);
    // then
    then(pageMapper).should(times(1)).fromSlice(any(Slice.class), isNull());
  }

  @Test
  @DisplayName("채널이 존재하지 않으면 메시지 목록 조회가 실패해야 한다.")
  void should_fail_find_all_message_by_channel_id_when_channel_not_found() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 2);

    given(channelRepository.existsById(fixedUuid)).willReturn(false);
    // when, then
    assertThrows(ChannelNotFoundException.class, () -> {
      basicMessageService.findAllByChannelId(fixedUuid, cursor, pageable);
    });
    then(messageRepository).should(never())
        .findAllByChannelIdAndCreatedAtLessThan(fixedUuid, cursor, pageable);
  }

  @Test
  @DisplayName("메시지에 수정사항이 반영되어야 한다.")
  void should_update_message() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    Channel fixedChannel = new Channel(ChannelType.PUBLIC, "일반 채널", "일반 채널 입니다.");
    User fixedUser = new User("김코딩", "hello@hello.com", "1234", null);
    Message existingMessage = new Message("옛날 메시지", fixedChannel, fixedUser);
    MessageUpdateRequest request = new MessageUpdateRequest("새로운 메시지");

    given(messageRepository.findById(fixedUuid)).willReturn(Optional.of(existingMessage));
    // when
    basicMessageService.update(fixedUuid, request);
    // then
    assertEquals("새로운 메시지", existingMessage.getContent());
  }

  @Test
  @DisplayName("수정하려는 메시지가 없으면 메시지 수정에 실패해야 한다.")
  void should_fail_update_message_when_message_not_found() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("새로운 메시지");

    given(messageRepository.findById(fixedUuid)).willReturn(Optional.empty());
    // when, then
    assertThrows(MessageNotFoundException.class,
        () -> basicMessageService.update(fixedUuid, request));
    then(messageMapper).should(never()).toDto(any(Message.class));
  }

  @Test
  @DisplayName("메시지 삭제에 성공해야 한다.")
  void should_delete_message() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    Channel fixedChannel = new Channel(ChannelType.PUBLIC, "일반 채널", "일반 채널 입니다.");
    User fixedUser = new User("김코딩", "hello@hello.com", "1234", null);
    Message expectMessage = new Message("삭제할 메시지", fixedChannel, fixedUser);

    given(messageRepository.findById(fixedUuid)).willReturn(Optional.of(expectMessage));
    // when
    basicMessageService.delete(fixedUuid);
    // then
    then(messageRepository).should(times(1)).delete(expectMessage);
  }

  @Test
  @DisplayName("삭제하려는 메시지가 존재하지 않으면 삭제에 실패해야 한다.")
  void should_fail_delete_message_when_message_not_found() {
    // given
    UUID fixedUuid = UUID.randomUUID();

    given(messageRepository.findById(fixedUuid)).willReturn(Optional.empty());
    // when, then
    assertThrows(MessageNotFoundException.class, () -> {
      basicMessageService.delete(fixedUuid);
    });
    then(messageRepository).should(never()).delete(any(Message.class));
  }
}