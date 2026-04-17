package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicReadStatusServiceTest {

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private ReadStatusMapper readStatusMapper;

  @Mock
  EntityManager em;

  @InjectMocks
  private BasicReadStatusService readStatusService;

  private UUID userId;
  private UUID channelId;
  private UUID readStatusId;
  private User user;
  private Channel channel;
  private ReadStatus readStatus;
  private ReadStatusDto readStatusDto;
  private Instant lastReadAt;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = User.create("test", "test@test.com", "password", null);
    ReflectionTestUtils.setField(user, "id", userId);

    channelId = UUID.randomUUID();
    channel = Channel.create(ChannelType.PUBLIC, "public", null);
    ReflectionTestUtils.setField(channel, "id", channelId);

    lastReadAt = Instant.now();

    readStatusId = UUID.randomUUID();
    readStatus = ReadStatus.create(user, channel, lastReadAt);
    readStatusDto = new ReadStatusDto(readStatusId, userId, channelId, lastReadAt);

  }

  @Test
  @DisplayName("성공: 읽기 상태 정보 생성 성공")
  void createReadStatusSuccess() {
    //given
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(userId, channelId)).willReturn(
        Optional.empty());
    given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
    given(readStatusMapper.toDto(eq(readStatus))).willReturn(readStatusDto);

    //when
    ReadStatusDto result = readStatusService.create(request);

    //then
    assertNotNull(result);
    ArgumentCaptor<ReadStatus> captor = ArgumentCaptor.forClass(ReadStatus.class);
    then(readStatusRepository).should().save(captor.capture());
    ReadStatus capturedReadStatus = captor.getValue();

    assertEquals(user, capturedReadStatus.getUser());
    assertEquals(channel, capturedReadStatus.getChannel());
    assertEquals(lastReadAt, capturedReadStatus.getLastReadAt());
  }

  @Test
  @DisplayName("실패: 읽기 상태 정보가 존재하는 경우 실패")
  void createReadStatusWithAlreadyExistedReadStatusFailure() {
    //given
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(userId, channelId)).willReturn(
        Optional.of(readStatus));
    //when & then
    assertThrows(ReadStatusAlreadyExistException.class, () -> readStatusService.create(request));
  }

  @Test
  @DisplayName("성공: 읽기 상태 정보 조회 성공")
  void findReadStatusSuccess() {
    //given
    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));

    //when
    ReadStatus result = readStatusService.find(readStatusId);

    //then
    assertNotNull(result);
    assertEquals(readStatus, result);
  }

  @Test
  @DisplayName("실패: 읽기 상태 정보 조회 실패")
  void findReadStatusFailure() {
    //given
    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());
    //when & then
    assertThrows(ReadStatusNotFoundException.class,
        () -> readStatusService.find(readStatusId));
  }

  @Test
  @DisplayName("성공: 사용자 아이디로 읽기 상태 정보 목록 조회 성공")
  void findAllReadStatusByUserIdSuccess() {
    //given
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));
    given(readStatusMapper.toDto(eq(readStatus))).willReturn(readStatusDto);
    //when
    List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);

    //then
    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("실패: 사용자 아이디로 읽기 상태 정보 목록 조회 시 빈 리스트 반환")
  void findAllReadStatusByUserIdFailure() {
    //given
    given(readStatusRepository.findAllByUserId(userId)).willReturn(Collections.emptyList());

    //when
    List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);

    //then
    assertEquals(0, result.size());
  }

  @Test
  @DisplayName("성공: 읽기 상태 정보 수정 성공")
  void updateReadStatusSuccess() {
    //given
    Instant newLastReadAt = lastReadAt.plusSeconds(60);
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt);
    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toDto(eq(readStatus))).willReturn(readStatusDto);

    //when
    ReadStatusDto result = readStatusService.update(readStatusId, request);

    //then
    assertNotNull(result);
    assertEquals(newLastReadAt, readStatus.getLastReadAt());

  }

  @Test
  @DisplayName("실패: 읽기 상태 정보 수정 실패")
  void updateReadStatusFailure() {
    //given
    Instant newLastReadAt = lastReadAt.plusSeconds(60);
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt);
    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());
    //when & then
    assertThrows(ReadStatusNotFoundException.class,
        () -> readStatusService.update(readStatusId, request));
  }

  @Test
  @DisplayName("성공: 읽기 상태 정보 삭제 성공")
  void deleteReadStatusSuccess() {
    //given
    given(readStatusRepository.existsById(readStatusId)).willReturn(true);

    //when
    readStatusService.delete(readStatusId);

    //then
    then(readStatusRepository).should().deleteById(readStatusId);
  }

  @Test
  @DisplayName("실패: 읽기 상태 정보 삭제 실패")
  void deleteReadStatusFailure() {
    //given
    given(readStatusRepository.existsById(readStatusId)).willReturn(false);
    //when & then
    assertThrows(ReadStatusNotFoundException.class,
        () -> readStatusService.delete(readStatusId));
  }
}