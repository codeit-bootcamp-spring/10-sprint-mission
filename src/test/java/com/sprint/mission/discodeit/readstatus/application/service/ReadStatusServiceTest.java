package com.sprint.mission.discodeit.readstatus.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.readstatus.application.mapper.ReadStatusDomainMapper;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusErrorCode;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusException;
import com.sprint.mission.discodeit.readstatus.domain.provider.ReadStatusChannelResolver;
import com.sprint.mission.discodeit.readstatus.domain.provider.ReadStatusUserResolver;
import com.sprint.mission.discodeit.readstatus.domain.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.readstatus.presentation.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.support.mapper.DomainMapperContainer;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadStatusServiceTest {

  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private ReadStatusUserResolver userProvider;
  @Mock
  private ReadStatusChannelResolver channelProvider;
  @Spy
  private ReadStatusDomainMapper readStatusMapper = DomainMapperContainer.get(
      ReadStatusDomainMapper.class);

  @InjectMocks
  private ReadStatusService readStatusService;

  @Test
  @DisplayName("findAllByUserId - 유저 ID로 모든 읽음 상태를 정상 조회한다.")
  void findAllByUserId_success() {
    UUID userId = UUID.randomUUID();
    ReadStatus readStatus = mock(ReadStatus.class);

    given(readStatusRepository.findAllByUser_Id(userId)).willReturn(List.of(readStatus));

    List<ReadStatus> result = readStatusService.findAllByUserId(userId);

    assertThat(result).containsExactly(readStatus);
  }

  @Test
  @DisplayName("create - 단일 읽음 상태를 정상 생성한다.")
  void create_single_success() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    User user = mock(User.class);
    Channel channel = mock(Channel.class);

    given(readStatusRepository.existsByChannel_IdAndUser_Id(channelId, userId)).willReturn(false);
    given(userProvider.getProxyOrThrow(userId)).willReturn(user);
    given(channelProvider.getProxyOrThrow(channelId)).willReturn(channel);

    ReadStatus result = readStatusService.create(request);

    assertThat(result).isNotNull();
    verify(readStatusRepository).save(any(ReadStatus.class));
  }

  @Test
  @DisplayName("create - 이미 존재하는 읽음 상태 생성 시 예외를 던진다.")
  void create_single_fail_already_exist() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    given(readStatusRepository.existsByChannel_IdAndUser_Id(channelId, userId)).willReturn(true);

    assertThatThrownBy(() -> readStatusService.create(request))
        .isInstanceOf(ReadStatusException.class)
        .hasFieldOrPropertyWithValue("errorCode", ReadStatusErrorCode.READSTATUS_ALREADY_EXIST);
  }

  @Test
  @DisplayName("create - 다수의 참여자에 대해 읽음 상태를 정상 생성한다.")
  void create_multiple_success() {
    UUID channelId = UUID.randomUUID();
    List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    Channel channel = mock(Channel.class);
    List<User> users = List.of(mock(User.class), mock(User.class));
    List<ReadStatus> statuses = List.of(mock(ReadStatus.class), mock(ReadStatus.class));

    given(readStatusRepository.countByChannel_IdAndUser_IdIn(channelId, participantIds)).willReturn(
        0L);
    given(channelProvider.getProxy(channelId)).willReturn(channel);
    given(userProvider.getProxy(participantIds)).willReturn(users);
    given(readStatusMapper.toEntityFrom(channel, users, true)).willReturn(statuses);

    readStatusService.create(channelId, participantIds, true);

    verify(readStatusRepository).saveAll(statuses);
  }

  @Test
  @DisplayName("create - 다수 참여자 생성 중 이미 존재하는 읽음 상태가 있으면 예외를 던진다.")
  void create_multiple_fail_already_exist() {
    UUID channelId = UUID.randomUUID();
    List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());

    given(readStatusRepository.countByChannel_IdAndUser_IdIn(channelId, participantIds)).willReturn(
        1L);

    assertThatThrownBy(() -> readStatusService.create(channelId, participantIds, true))
        .isInstanceOf(ReadStatusException.class)
        .hasFieldOrPropertyWithValue("errorCode", ReadStatusErrorCode.READSTATUS_ALREADY_EXIST);
  }

  @Test
  @DisplayName("find - ID로 읽음 상태를 정상 조회한다.")
  void find_success() {
    UUID id = UUID.randomUUID();
    ReadStatus status = mock(ReadStatus.class);

    given(readStatusRepository.findById(id)).willReturn(Optional.of(status));

    ReadStatus result = readStatusService.find(id);

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("update - 읽음 상태를 정상 수정한다.")
  void update_success() {
    UUID id = UUID.randomUUID();
    ReadStatusUpdateRequest request = mock(ReadStatusUpdateRequest.class);
    ReadStatus status = mock(ReadStatus.class);

    given(readStatusRepository.findById(id)).willReturn(Optional.of(status));

    ReadStatus result = readStatusService.update(id, request);

    assertThat(result).isNotNull();
    verify(readStatusMapper).partialUpdate(request, status);
  }

  @Test
  @DisplayName("delete - 읽음 상태를 정상 삭제한다.")
  void delete_success() {
    UUID id = UUID.randomUUID();
    ReadStatus status = mock(ReadStatus.class);

    given(readStatusRepository.findById(id)).willReturn(Optional.of(status));

    readStatusService.delete(id);

    verify(readStatusRepository).delete(status);
  }
}
