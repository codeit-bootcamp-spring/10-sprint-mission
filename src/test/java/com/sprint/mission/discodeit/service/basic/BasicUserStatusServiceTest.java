package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicUserStatusServiceTest {

    @Mock private UserStatusRepository userStatusRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserStatusMapper userStatusMapper;

    @InjectMocks
    private BasicUserStatusService basicUserStatusService;

    @Test
    @DisplayName("유효한 요청으로 UserStatus를 생성한다.")
    void create_success() {
        // given
        UUID userId = UUID.randomUUID();
        CreateUserStatusRequestDto request = new CreateUserStatusRequestDto(userId);
        User user = new User("tester", "t@t.com", "pass");
        UserStatus userStatus = new UserStatus(user);
        UserStatusDto expectedDto = new UserStatusDto(UUID.randomUUID(), userId, Instant.now());

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
        given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(expectedDto);

        // when
        UserStatusDto result = basicUserStatusService.create(request);

        // then
        assertEquals(expectedDto, result);
        verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("이미 UserStatus가 존재하면 예외가 발생한다.")
    void create_fail_already_exists() {
        // given
        UUID userId = UUID.randomUUID();
        CreateUserStatusRequestDto request = new CreateUserStatusRequestDto(userId);
        User user = new User("tester", "t@t.com", "pass");
        UserStatus existing = new UserStatus(user);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(existing));

        // when & then
        assertThrows(NoSuchElementException.class,
            () -> basicUserStatusService.create(request));
    }

    @Test
    @DisplayName("userId로 UserStatus를 수정 시 현재 시간으로 lastActiveAt이 갱신된다.")
    void updateByUserId_no_request_body_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("tester", "t@t.com", "pass");
        UserStatus userStatus = new UserStatus(user);
        UserStatusDto expectedDto = new UserStatusDto(UUID.randomUUID(), userId, Instant.now());

        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toDto(userStatus)).willReturn(expectedDto);

        // when
        UserStatusDto result = basicUserStatusService.updateByUserId(userId);

        // then
        assertNotNull(userStatus.getLastActiveAt());
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("특정 시간으로 UserStatus를 수정한다.")
    void updateByUserId_with_request_body_success() {
        // given
        UUID userId = UUID.randomUUID();
        Instant specificTime = Instant.now().minusSeconds(30);
        UpdateUserStatusRequestDto request = new UpdateUserStatusRequestDto(specificTime);
        User user = new User("tester", "t@t.com", "pass");
        UserStatus userStatus = new UserStatus(user);
        UserStatusDto expectedDto = new UserStatusDto(UUID.randomUUID(), userId, specificTime);

        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toDto(userStatus)).willReturn(expectedDto);

        // when
        UserStatusDto result = basicUserStatusService.updateByUserId(userId, request);

        // then
        assertEquals(specificTime, userStatus.getLastActiveAt());
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("존재하지 않는 userId로 수정 시 NoSuchElementException이 발생한다.")
    void updateByUserId_fail_not_found() {
        // given
        UUID fakeUserId = UUID.randomUUID();
        given(userStatusRepository.findByUserId(fakeUserId)).willReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class,
            () -> basicUserStatusService.updateByUserId(fakeUserId));
    }

    @Test
    @DisplayName("UserStatus를 삭제한다.")
    void delete_success() {
        // given
        UUID userStatusId = UUID.randomUUID();
        User user = new User("tester", "t@t.com", "pass");
        UserStatus userStatus = new UserStatus(user);

        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));

        // when
        basicUserStatusService.delete(userStatusId);

        // then
        verify(userStatusRepository).delete(userStatus);
    }

    @Test
    @DisplayName("isOnline - 5분 이내 활동 유저는 온라인 상태다.")
    void userStatus_isOnline_true() {
        // given
        User user = new User("tester", "t@t.com", "pass");
        UserStatus userStatus = new UserStatus(user);
        // 방금 생성된 UserStatus는 lastActiveAt이 현재 시각이므로 온라인

        // when & then
        assertTrue(userStatus.isOnline());
    }

    @Test
    @DisplayName("isOnline - 5분 초과 비활동 유저는 오프라인 상태다.")
    void userStatus_isOnline_false() {
        // given
        User user = new User("tester", "t@t.com", "pass");
        UserStatus userStatus = new UserStatus(user);
        // 10분 전으로 lastActiveAt 강제 설정
        org.springframework.test.util.ReflectionTestUtils.setField(
            userStatus, "lastActiveAt", Instant.now().minusSeconds(601));

        // when & then
        assertFalse(userStatus.isOnline());
    }
}