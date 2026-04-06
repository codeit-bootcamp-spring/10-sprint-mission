package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
class BasicUserStatusServiceTest {

  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusMapper userStatusMapper;

  @InjectMocks
  private BasicUserStatusService basicUserStatusService;

  @Test
  @DisplayName("새로운 유저 상태 생성")
  void create_newUserStatus() {
    // given
    User user = new User("testuser", "test@example.com");
    UUID userId = user.getId();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId);
    UserStatus newUserStatus = new UserStatus(user, Instant.now());
    UserStatusDto expectedDto = new UserStatusDto(newUserStatus.getId(), userId,
        newUserStatus.getLastActiveAt());

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.empty());
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(newUserStatus);
    given(userStatusMapper.toDto(newUserStatus)).willReturn(expectedDto);

    // when
    UserStatusDto result = basicUserStatusService.create(request);

    // then
    assertThat(result).isEqualTo(expectedDto);
    verify(userStatusRepository).save(any(UserStatus.class));
  }

  @Test
  @DisplayName("기존 유저 상태 업데이트")
  void create_existingUserStatus() {
    // given
    User user = new User("testuser", "test@example.com");
    UUID userId = user.getId();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId);
    UserStatus existingStatus = new UserStatus(user, Instant.now().minusSeconds(600));
    UserStatusDto expectedDto = new UserStatusDto(existingStatus.getId(), userId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.of(existingStatus));
    given(userStatusMapper.toDto(existingStatus)).willReturn(expectedDto);

    // when
    UserStatusDto result = basicUserStatusService.create(request);

    // then
    assertThat(result).isEqualTo(expectedDto);
    assertThat(existingStatus.getLastActiveAt()).isAfter(Instant.now().minusSeconds(10));
  }

  @Test
  @DisplayName("ID로 유저 상태 찾기")
  void find_success() {
    // given
    UUID statusId = UUID.randomUUID();
    User user = new User("test", "t@t.com");
    UserStatus status = new UserStatus(user, Instant.now());
    UserStatusDto expectedDto = new UserStatusDto(statusId, user.getId(), status.getLastActiveAt());

    given(userStatusRepository.findById(statusId)).willReturn(Optional.of(status));
    given(userStatusMapper.toDto(status)).willReturn(expectedDto);

    // when
    UserStatusDto result = basicUserStatusService.find(statusId);

    // then
    assertThat(result).isEqualTo(expectedDto);
  }

  @Test
  @DisplayName("ID로 유저 상태 찾기 실패")
  void find_fail_notFound() {
    // given
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicUserStatusService.find(statusId))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("모든 유저 상태 찾기")
  void findAll() {
    // given
    User user = new User("test", "t@t.com");
    UserStatus status = new UserStatus(user, Instant.now());
    List<UserStatus> statuses = Collections.singletonList(status);
    UserStatusDto dto = new UserStatusDto(status.getId(), user.getId(), status.getLastActiveAt());

    given(userStatusRepository.findAll()).willReturn(statuses);
    given(userStatusMapper.toDto(status)).willReturn(dto);

    // when
    List<UserStatusDto> results = basicUserStatusService.findAll();

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0)).isEqualTo(dto);
  }

  @Test
  @DisplayName("update 메서드 테스트")
  void update_shouldReturnNull() {
    UserStatusUpdateRequest request = new UserStatusUpdateRequest();
    UserStatusDto result = basicUserStatusService.update(request);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("UserID로 유저 상태 업데이트")
  void updateByUserId_success() {
    // given
    User user = new User("test", "t@t.com");
    UUID userId = user.getId();
    UserStatus status = new UserStatus(user, Instant.now());
    Instant newTime = Instant.now().plusSeconds(100);
    UserStatusUpdateRequest request = new UserStatusUpdateRequest();
    request.setNewLastActiveAt(newTime);
    UserStatusDto expectedDto = new UserStatusDto(status.getId(), userId, newTime);

    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.of(status));
    given(userStatusMapper.toDto(status)).willReturn(expectedDto);

    // when
    UserStatusDto result = basicUserStatusService.updateByUserId(userId, request);

    // then
    assertThat(result).isEqualTo(expectedDto);
    assertThat(status.getLastActiveAt()).isEqualTo(newTime);
  }

  @Test
  @DisplayName("UserID로 유저 상태 업데이트 - 요청 시간이 null일 경우")
  void updateByUserId_nullRequestTime() {
    // given
    User user = new User("test", "t@t.com");
    UUID userId = user.getId();
    UserStatus status = new UserStatus(user, Instant.now().minusSeconds(600));
    UserStatusUpdateRequest request = new UserStatusUpdateRequest();

    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.of(status));
    given(userStatusMapper.toDto(any(UserStatus.class))).willAnswer(invocation -> {
      UserStatus answeredStatus = invocation.getArgument(0);
      return new UserStatusDto(answeredStatus.getId(), answeredStatus.getUserId(),
          answeredStatus.getLastActiveAt());
    });

    // when
    UserStatusDto result = basicUserStatusService.updateByUserId(userId, request);

    // then
    assertThat(result.lastActiveAt()).isAfter(Instant.now().minusSeconds(10));
  }


  @Test
  @DisplayName("UserID로 유저 상태 업데이트 실패 - 못 찾음")
  void updateByUserId_fail_notFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest();
    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicUserStatusService.updateByUserId(userId, request))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("ID로 유저 상태 삭제")
  void delete_success() {
    // given
    UUID statusId = UUID.randomUUID();

    // when
    basicUserStatusService.delete(statusId);

    // then
    verify(userStatusRepository).deleteById(statusId);
  }
}
