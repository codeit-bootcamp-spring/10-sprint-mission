package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
class BasicUserStatusServiceTest {

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusMapper userStatusMapper;

  @InjectMocks
  private BasicUserStatusService userStatusService;

  private UUID userStatusId;
  private UUID userId;
  private Instant lastActiveAt;
  private User user;
  private UserStatus userStatus;
  private UserStatusDto userStatusDto;

  @BeforeEach
  void setUp() {
    userStatusId = UUID.randomUUID();
    userId = UUID.randomUUID();
    lastActiveAt = Instant.now();

    user = User.create("test", "test@test.com", "password", null);
    ReflectionTestUtils.setField(user, "id", userId);

    userStatus = UserStatus.create(user, lastActiveAt);
    ReflectionTestUtils.setField(userStatus, "id", userStatusId);

    userStatusDto = new UserStatusDto(userStatusId, userId, lastActiveAt);
  }

  @Test
  @DisplayName("성공: 사용자 상태 생성 성공")
  void createUserStatusSuccess() {
    //given
    UserStatusCreateDto request = new UserStatusCreateDto(userId);

    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(userStatusRepository.existsByUserId(userId)).willReturn(false);
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);

    //when
    UserStatus result = userStatusService.create(request);

    //then
    assertNotNull(result);
    ArgumentCaptor<UserStatus> captor = ArgumentCaptor.forClass(UserStatus.class);
    then(userStatusRepository).should().save(captor.capture());
    UserStatus capturedUserStatus = captor.getValue();

    assertEquals(user, capturedUserStatus.getUser());
    assertEquals(user.getUserStatus(), capturedUserStatus);
  }

  @Test
  @DisplayName("실패: 이미 상태가 있는 사용자에 대한 상태 생성 시도 실패")
  void createUserStatusWithExistingStatusFailure() {
    //given
    UserStatusCreateDto request = new UserStatusCreateDto(userId);
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(userStatusRepository.existsByUserId(userId)).willReturn(true);

    //when & then
    assertThrows(UserStatusAlreadyExistException.class, () -> userStatusService.create(request));
  }

  @Test
  @DisplayName("실패: 존재하지 않는 사용자에 대한 상태 생성 시도 실패")
  void createUserStatusWithNotExistedUserFailure() {
    //given
    UserStatusCreateDto request = new UserStatusCreateDto(userId);
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    //when & then
    assertThrows(UserNotFoundException.class, () -> userStatusService.create(request));
  }

  @Test
  @DisplayName("성공: 사용자 상태 조회 성공")
  void findUserStatusSuccess() {
    //given
    given(userStatusRepository.findById(eq(userStatusId))).willReturn(Optional.of(userStatus));

    //when
    UserStatus result = userStatusService.find(userStatusId);

    //then
    assertNotNull(result);
    assertEquals(userStatus, result);
  }

  @Test
  @DisplayName("실패: 존재하지 않는 사용자 상태 조회 실패")
  void findUserStatusWithNotExistedIdFailure() {
    //given
    given(userStatusRepository.findById(eq(userStatusId))).willReturn(Optional.empty());

    //when & then
    assertThrows(UserStatusNotFoundException.class, () -> userStatusService.find(userStatusId));
  }

  @Test
  @DisplayName("성공: 전체 사용자 상태 목록 조회 성공")
  void findAllUserStatusesSuccess() {
    //given
    given(userStatusRepository.findAll()).willReturn(List.of(userStatus));

    //when
    List<UserStatus> result = userStatusService.findAll();

    //then
    assertNotNull(result);
    assertEquals(userStatus, result.get(0));
  }

  @Test
  @DisplayName("성공: 사용자 상태 수정 성공")
  void updateUserStatus_Success() {
    //given
    Instant newLastActiveAt = Instant.now().plusSeconds(60);
    UserStatusUpdateDto request = new UserStatusUpdateDto(newLastActiveAt);

    given(userStatusRepository.findById(eq(userStatusId))).willReturn(Optional.of(userStatus));

    //when
    UserStatus result = userStatusService.update(userStatusId, request);

    //then
    assertNotNull(result);
    assertEquals(newLastActiveAt, result.getLastActiveAt());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 사용자 상태 수정 시도 시 실패")
  void updateUserStatusWithNonExistedIdFailure() {
    //given
    Instant newLastActiveAt = Instant.now().plusSeconds(60);
    UserStatusUpdateDto request = new UserStatusUpdateDto(newLastActiveAt);

    given(userStatusRepository.findById(eq(userStatusId))).willReturn(Optional.empty());

    //when & then
    assertThrows(UserStatusNotFoundException.class,
        () -> userStatusService.update(userStatusId, request));
  }

  @Test
  @DisplayName("사용자 ID로 상태 수정 성공")
  void updateUserStatusByUserId_Success() {
    //given
    Instant newLastActiveAt = Instant.now().plusSeconds(60);
    UserStatusUpdateDto request = new UserStatusUpdateDto(newLastActiveAt);

    given(userStatusRepository.findByUserId(eq(userId))).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    //when
    UserStatusDto result = userStatusService.updateByUserId(userId, request);

    //then
    assertNotNull(result);
    assertEquals(newLastActiveAt, userStatus.getLastActiveAt());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 사용자 ID로 상태 수정 시도 실패")
  void updateUserStatusByUserIdWithNotExistedUserIdFailure() {
    //given
    Instant newLastActiveAt = Instant.now().plusSeconds(60);
    UserStatusUpdateDto request = new UserStatusUpdateDto(newLastActiveAt);

    given(userStatusRepository.findByUserId(eq(userId))).willReturn(Optional.empty());

    //when & then
    assertThrows(UserStatusNotFoundException.class,
        () -> userStatusService.updateByUserId(userId, request));
  }

  @Test
  @DisplayName("성공: 사용자 상태 삭제 성공")
  void deleteUserStatusSuccess() {
    //given
    given(userStatusRepository.existsById(eq(userStatusId))).willReturn(true);

    //when
    userStatusService.delete(userStatusId);

    //then
    then(userStatusRepository).should().deleteById(eq(userStatusId));
  }

  @Test
  @DisplayName("실패: 존재하지 않는 사용자 상태 삭제 시도 실패")
  void deleteUserStatusWithNotExistedIdFailure() {
    //given
    given(userStatusRepository.existsById(eq(userStatusId))).willReturn(false);

    //when & then
    assertThrows(UserStatusNotFoundException.class, () -> userStatusService.delete(userStatusId));
  }
}