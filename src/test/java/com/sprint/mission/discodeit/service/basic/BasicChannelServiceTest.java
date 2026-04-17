package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.message.LastMessageTimeDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.NotAllowedInPrivateChannelException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelMapper channelMapper;
  @Mock
  private BinaryContentRepository binaryContentRepository;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("성공: 공개 채널(이름, 설명) 생성 성공")
  void createPublicChannelSuccess() {
    //given
    PublicChannelCreateRequest dto = new PublicChannelCreateRequest("public", "test");
    Channel channel = Channel.create(ChannelType.PUBLIC, dto.name(), dto.description());
    List<User> users = List.of(mock(User.class), mock(User.class), mock(User.class));
    ChannelDto channelDto = new ChannelDto(channel.getId(), ChannelType.PUBLIC, dto.name()
        , dto.description(), Instant.now(), Instant.now(), Instant.now(),
        List.of(mock(UserDto.class), mock(UserDto.class), mock(UserDto.class)));

    given(channelMapper.toEntity(dto)).willReturn(channel);
    given(userRepository.findAllFetchUserInfo()).willReturn(users);
    given(channelMapper.toDto(channel)).willReturn(channelDto);

    //when
    ChannelDto result = channelService.create(dto);

    //then
    assertNotNull(result);
    assertEquals(ChannelType.PUBLIC, result.type());
    assertEquals("public", result.name());

    // 채널 저장 검증
    ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
    then(channelRepository).should().save(channelCaptor.capture());
    Channel capturedChannel = channelCaptor.getValue();
    assertEquals(dto.name(), capturedChannel.getName());
    assertEquals(dto.description(), capturedChannel.getDescription());

    // 읽기 상태 저장 검증
    ArgumentCaptor<List<ReadStatus>> listCaptor = ArgumentCaptor.forClass(List.class);
    then(readStatusRepository).should().saveAll(listCaptor.capture());
    assertEquals(users.size(), listCaptor.getValue().size()); // 멤버 수만큼 읽기 상태저장
  }


  @Test
  @DisplayName("성공: 비공개 채널(모든 멤버가 유효) 생성 성공")
  void createPrivateChannelSuccess() {
    //given
    PrivateChannelCreateRequest dto = new PrivateChannelCreateRequest(
        List.of(UUID.randomUUID(), UUID.randomUUID()));
    Channel channel = Channel.create(ChannelType.PRIVATE, null, null);
    List<User> users = List.of(mock(User.class), mock(User.class));
    ChannelDto channelDto = new ChannelDto(channel.getId(), ChannelType.PRIVATE, null, null,
        Instant.now(), Instant.now(), Instant.now(),
        List.of(mock(UserDto.class), mock(UserDto.class)));

    given(channelMapper.toEntity(dto)).willReturn(channel);
    given(userRepository.findAllByIdFetchUserInfo(dto.memberIds())).willReturn(users);
    given(channelMapper.toDto(channel)).willReturn(channelDto);

    //when
    ChannelDto result = channelService.create(dto);

    //then
    assertNotNull(result);
    assertEquals(ChannelType.PRIVATE, result.type());
    assertEquals(dto.memberIds().size(), result.participants().size());

    //채널 저장 검증
    ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
    then(channelRepository).should().save(channelCaptor.capture());
    Channel capturedChannel = channelCaptor.getValue();
    assertEquals(ChannelType.PRIVATE, capturedChannel.getType());

    // 읽기 상태 저장 검증
    ArgumentCaptor<List<ReadStatus>> listCaptor = ArgumentCaptor.forClass(List.class);
    then(readStatusRepository).should().saveAll(listCaptor.capture());
    assertEquals(users.size(), listCaptor.getValue().size()); // 멤버 수만큼 읽기 상태저장
  }

  @Test
  @DisplayName("실패: 비공개 채널(모든 멤버가 유효하지 않음) 생성 실패")
  void createPrivateChannelFailure() {
    //given
    PrivateChannelCreateRequest dto = new PrivateChannelCreateRequest(
        List.of(UUID.randomUUID(), UUID.randomUUID()));
    Channel channel = Channel.create(ChannelType.PRIVATE, null, null);
    List<User> users = List.of();//멤버 유효 0

    given(channelMapper.toEntity(dto)).willReturn(channel);
    given(userRepository.findAllByIdFetchUserInfo(dto.memberIds())).willReturn(users);

    //when & then
    assertThrows(UserNotFoundException.class, () -> channelService.create(dto));
  }

  @Test
  @DisplayName("실패: 비공개 채널(일부 멤버가 유효하지 않음) 생성 실패")
  void createPrivateChannelFailureWithSomeInvalidUser() {
    //given
    PrivateChannelCreateRequest dto = new PrivateChannelCreateRequest(
        List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));//요청 멤버 3명
    Channel channel = Channel.create(ChannelType.PRIVATE, null, null);
    List<User> users = List.of(mock(User.class), mock(User.class));//유효멤버 2명

    given(channelMapper.toEntity(dto)).willReturn(channel);
    given(userRepository.findAllByIdFetchUserInfo(dto.memberIds())).willReturn(users);

    //when & then
    assertThrows(UserNotFoundException.class, () -> channelService.create(dto));
  }

  @Test
  @DisplayName("성공: 공개 채널의 정보(이름, 설명) 수정 성공")
  void updatePublicChannelSuccess() {
    //given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest dto = new PublicChannelUpdateRequest("newName", "newDesc");
    Channel exsitedChannel = Channel.create(ChannelType.PUBLIC, "oldName", "oldDesc");
    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, dto.name(),
        dto.description(), Instant.now(), Instant.now(), Instant.now(),
        List.of(mock(UserDto.class), mock(UserDto.class)));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(exsitedChannel));
    given(channelMapper.toDto(exsitedChannel)).willReturn(channelDto);

    //when
    ChannelDto result = channelService.update(channelId, dto);

    //then
    assertEquals(dto.name(), exsitedChannel.getName());
    assertEquals(dto.description(), exsitedChannel.getDescription());
    assertEquals(dto.name(), result.name());
    assertEquals(dto.description(), result.description());
  }

  @Test
  @DisplayName("실패: 비공개 채널 정보 수정 실패")
  void updatePrivateChannelFailure() {
    //given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest dto = new PublicChannelUpdateRequest("newName", "newDesc");
    Channel privateChannel = Channel.create(ChannelType.PRIVATE, null, null);
    when(channelRepository.findById(channelId)).thenReturn(Optional.of(privateChannel));

    //when & then
    assertThrows(NotAllowedInPrivateChannelException.class, () ->
        channelService.update(channelId, dto));
  }

  @Test
  @DisplayName("성공: 사용자 아이디로 채널 목록 조회 성공")
  void findAllByUserIdSuccess() {
    //given
    UUID userId = UUID.randomUUID();
    User user = User.create("test", "test@test.com", "test", null);
    Channel c1 = Channel.create(ChannelType.PUBLIC, "name1", "desc1");
    Channel c2 = Channel.create(ChannelType.PRIVATE, null, null);
    UUID c1Id = UUID.randomUUID();
    UUID c2Id = UUID.randomUUID();
    Set<UUID> channelKeySet = Set.of(c1Id, c2Id);
    List<ReadStatus> readStatusesByUser = List.of(ReadStatus.create(user, c1, Instant.now()),
        ReadStatus.create(user, c2, Instant.now()));
    List<ReadStatus> readStatusesByChannel = List.of(ReadStatus.create(user, c1, Instant.now()),
        ReadStatus.create(user, c2, Instant.now()),
        ReadStatus.create(mock(User.class), c1, Instant.now()),
        ReadStatus.create(mock(User.class), c1, Instant.now()),
        ReadStatus.create(mock(User.class), c2, Instant.now()));//c1 멤버3, c2 멤버2
    List<LastMessageTimeDto> lastMessageTimeDtos = List.of(
        new LastMessageTimeDto(c1Id, Instant.now()));//c2는 메세지 없음

    ReflectionTestUtils.setField(c1, "id", c1Id);
    ReflectionTestUtils.setField(c2, "id", c2Id);

    given(userRepository.existsById(userId)).willReturn(true);
    given(readStatusRepository.findAllByUserIdFetchChannel(userId)).willReturn(readStatusesByUser);
    given(readStatusRepository.findAllByChannelIdInFetchUser(channelKeySet)).willReturn(
        readStatusesByChannel);
    given(messageRepository.findAllLastMessagesByChannelId(channelKeySet)).willReturn(
        lastMessageTimeDtos);
    given(channelMapper.toDto(eq(c1), any(), any())).willReturn(
        new ChannelDto(c1.getId(), c2.getType(), c1.getName(), c1.getDescription(),
            Instant.now().minusSeconds(10), null,
            null, List.of(mock(UserDto.class), mock(UserDto.class), mock(UserDto.class))));
    given(channelMapper.toDto(eq(c2), any(), any())).willReturn(
        new ChannelDto(c2.getId(), c2.getType(), c2.getName(), c2.getDescription(), Instant.now(),
            null, null,
            List.of(mock(UserDto.class), mock(UserDto.class))));

    //when
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    //then
    assertEquals(2, result.size());
    assertTrue(result.get(0).createdAt().isBefore(result.get(1).createdAt())); // 정렬 확인
  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 아이디로 채널 목록 조회 실패")
  void findAllByUserIdFailure() {
    //given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(false);

    //when & then
    assertThrows(UserNotFoundException.class, () -> channelService.findAllByUserId(userId));
  }

  @Test
  @DisplayName("성공: 유효한 채널 삭제 성공")
  void deleteChannelSuccess() {
    //given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(true);

    //when
    channelService.delete(channelId);

    //then
    InOrder inOrder = inOrder(binaryContentRepository, messageRepository,
        channelRepository);
    inOrder.verify(binaryContentRepository).bulkDeleteByChannelId(channelId);
    inOrder.verify(messageRepository).bulkDeleteByChannelId(channelId);
    inOrder.verify(channelRepository).deleteById(channelId);
  }

  @Test
  @DisplayName("실패: 유효하지 않은 채널 삭제 실패")
  void deleteChannelFailure() {
    //given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(false);

    //when & then
    assertThrows(ChannelNotFoundException.class, () ->
        channelService.delete(channelId));
  }
}