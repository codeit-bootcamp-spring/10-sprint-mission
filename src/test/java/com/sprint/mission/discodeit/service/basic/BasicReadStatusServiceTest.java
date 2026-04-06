package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

  @InjectMocks
  private BasicReadStatusService basicReadStatusService;

  @Test
  @DisplayName("읽기 상태 생성 성공")
  void createStatus_success() {
    // given
    User user = new User("user", "user@mail.com");
    Channel channel = new Channel("channel", ChannelType.PUBLIC);
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(user.getId(), channel.getId(),
        Instant.now());
    ReadStatus readStatus = new ReadStatus(user, channel, request.getLastReadAt());
    ReadStatusDto expectedDto = new ReadStatusDto(readStatus.getId(), user.getId(), channel.getId(),
        request.getLastReadAt());

    given(
        readStatusRepository.findByUser_IdAndChannel_Id(user.getId(), channel.getId())).willReturn(
        Optional.empty());
    given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
    given(channelRepository.findById(channel.getId())).willReturn(Optional.of(channel));
    given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expectedDto);

    // when
    ReadStatusDto result = basicReadStatusService.createStatus(request);

    // then
    assertThat(result).isEqualTo(expectedDto);
    verify(readStatusRepository).save(any(ReadStatus.class));
  }

  @Test
  @DisplayName("읽기 상태 생성 실패 - 이미 존재")
  void createStatus_fail_alreadyExists() {
    // given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    given(readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)).willReturn(
        Optional.of(new ReadStatus(new User("u", "e"), new Channel("c", ChannelType.PUBLIC))));

    // when & then
    assertThatThrownBy(() -> basicReadStatusService.createStatus(request))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("ID로 읽기 상태 찾기 성공")
  void findStatus_success() {
    // given
    UUID statusId = UUID.randomUUID();
    ReadStatus readStatus = new ReadStatus(new User("u", "e"),
        new Channel("c", ChannelType.PUBLIC));
    ReadStatusDto expectedDto = new ReadStatusDto(statusId, UUID.randomUUID(), UUID.randomUUID(),
        Instant.now());

    given(readStatusRepository.findById(statusId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expectedDto);

    // when
    ReadStatusDto result = basicReadStatusService.findStatus(statusId);

    // then
    assertThat(result).isEqualTo(expectedDto);
  }

  @Test
  @DisplayName("UserID로 모든 읽기 상태 찾기")
  void findAllByUserId() {
    // given
    UUID userId = UUID.randomUUID();
    ReadStatus readStatus = new ReadStatus(new User("u", "e"),
        new Channel("c", ChannelType.PUBLIC));
    List<ReadStatus> statuses = Collections.singletonList(readStatus);
    ReadStatusDto dto = new ReadStatusDto(readStatus.getId(), userId, UUID.randomUUID(),
        Instant.now());

    given(readStatusRepository.findAllByUser_Id(userId)).willReturn(statuses);
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(dto);

    // when
    List<ReadStatusDto> results = basicReadStatusService.findAllByUserId(userId);

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0)).isEqualTo(dto);
  }

  @Test
  @DisplayName("읽기 상태 업데이트 성공")
  void updateStatus_success() {
    // given
    UUID statusId = UUID.randomUUID();
    ReadStatus readStatus = new ReadStatus(new User("u", "e"), new Channel("c", ChannelType.PUBLIC),
        Instant.now());
    Instant newTime = Instant.now().plusSeconds(100);
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest();
    request.setNewLastReadAt(newTime);
    ReadStatusDto expectedDto = new ReadStatusDto(statusId, null, null, newTime);

    given(readStatusRepository.findById(statusId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expectedDto);

    // when
    ReadStatusDto result = basicReadStatusService.updateStatus(statusId, request);

    // then
    assertThat(result).isEqualTo(expectedDto);
    assertThat(readStatus.getLastReadAt()).isEqualTo(newTime);
  }

  @Test
  @DisplayName("읽기 상태 업데이트 성공 - 요청 시간이 null")
  void updateStatus_success_nullTime() {
    // given
    UUID statusId = UUID.randomUUID();
    ReadStatus readStatus = new ReadStatus(new User("u", "e"), new Channel("c", ChannelType.PUBLIC),
        Instant.now().minusSeconds(100));
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest();
    given(readStatusRepository.findById(statusId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toDto(any(ReadStatus.class))).willAnswer(inv -> {
      ReadStatus rs = inv.getArgument(0);
      return new ReadStatusDto(rs.getId(), rs.getUserId(), rs.getChannelId(), rs.getLastReadAt());
    });

    // when
    ReadStatusDto result = basicReadStatusService.updateStatus(statusId, request);

    // then
    assertThat(result.lastReadAt()).isAfter(Instant.now().minusSeconds(10));
  }

  @Test
  @DisplayName("읽기 상태 삭제 성공")
  void deleteStatus_success() {
    // given
    UUID statusId = UUID.randomUUID();

    // when
    basicReadStatusService.deleteStatus(statusId);

    // then
    verify(readStatusRepository).deleteById(statusId);
  }
}
