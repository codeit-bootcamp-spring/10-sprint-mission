package com.sprint.mission.discodeit.service.basic;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("사용자는 정상적으로 등록이 되어야 합니다.")
  void create_success() {
    // given
    User user = new User("김러키", "lucky@google.com", "123asd");
    UserCreateRequest request = new UserCreateRequest("김러키", "lucky@google.com", "123asd", null);
    UserResponse response = new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        true,
        null,
        null
    );

    given(userMapper.toResponse(any(), any(), any())).willReturn(response);
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userRepository.existsByUsername(request.userName())).willReturn(false);
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    // when
    UserResponse result = userService.create(request);

    // then
    then(userRepository).should().save(any(User.class));
    then(userRepository).should().existsByUsername(request.userName());
    then(userRepository).should().existsByEmail(request.email());
    assertThat(result).isNotNull();
    assertThat(result.userName()).isEqualTo(request.userName());
    assertThat(result.email()).isEqualTo(request.email());
  }

  @Test
  @DisplayName("이미 존재하는 이메일이 들어오면 예외가 발생해야 합니다.")
  void create_fail_duplicate_email() {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "김러키", "lucky@google.com", "123asd", null
    );

    given(userRepository.existsByEmail(request.email())).willReturn(true);

    // when, then
    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(UserEmailAlreadyExistsException.class);
  }


  @Test
  @DisplayName("사용자의 정보는 정상적으로 수정되어야 합니다.")
  void update_success() {
    //given
    //유저 객체 생성
    User user = new User("김러키", "lucky@google.com", "123asd");
    //유저 식별자를 받을 수 없으니 하나 생성
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");
    // 유저 엔티티 객체에도 식별자 삽입
    ReflectionTestUtils.setField(user, "id", userId);
    //수정 할 데이터 객체 생성
    UserUpdateRequest update = new UserUpdateRequest(
        userId,
        Optional.of("김러키_수정"),
        Optional.of("lucky_fix@google.com"),
        Optional.empty(),
        Optional.empty()
    );
    //수정 한 데이터를 반환할 객체 생성
    UserResponse response = new UserResponse(
        userId,
        "김러키_수정",
        "lucky_fix@google.com",
        true,
        null,
        null
    );
    // userId로 기존 사용자를 조회하면 user를 반환하도록 설정
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    // userMapper.toResponse(...)가 호출되면 response를 반환하도록 설정
    given(userMapper.toResponse(any(), any(), any())).willReturn(response);
    // save를 호출하면 user를 반환
    given(userRepository.save(any(User.class))).willReturn(user);
    // findByUserId가 호출 시 UserStatus를 반환
    given(userStatusRepository.findByUserId(userId))
        .willReturn(new UserStatus(user, Instant.now()));

    //when
    // result에 우리가 만들었던 수정 데이터 넣기
    UserResponse result = userService.update(update);

    //then
    // update 과정에서 userId로 조회가 호출되었는지 검증
    then(userRepository).should().findById(userId);
    // 수정된 User 엔티티가 save 되었는지 검증
    then(userRepository).should().save(any(User.class));
    //result객체 안에있는 이름이 하드코딩해서 결과값을 예상한것과 일치 하면 통과
    assertThat(result.userName()).isEqualTo("김러키_수정");
    //result객체 안에있는 이메일이 하드코딩해서 결과값을 예상한것과 일치 하면 통과
    assertThat(result.email()).isEqualTo("lucky_fix@google.com");
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 시 예외가 발생해야 합니다.")
  void update_fail_not_found() {
    //given
    //유저 식별자를 받을 수 없으니 하나 생성
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");
    //수정 할 데이터 객체 생성
    UserUpdateRequest update = new UserUpdateRequest(
        userId,
        Optional.of("김러키_수정"),
        Optional.of("lucky_fix@google.com"),
        Optional.empty(),
        Optional.empty()
    );
    // userId로 기존 사용자를 조회하면 사용자가 존재하지 않는다고 설정
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    //when, then
    //업데이트 실행 시 사용자가 없으므로 예외발생
    assertThatThrownBy(() -> userService.update(update))
        .isInstanceOf(UserNotFoundException.class);
  }


  @Test
  @DisplayName("사용자는 정상적으로 삭제되어야 합니다.")
  void delete_success() {
    // given
    User user = new User("김러키", "lucky@google.com", "123asd");
    //유저 식별자를 받을 수 없으니 하나 생성
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");
    // 유저 객체에 식별자 넣기
    ReflectionTestUtils.setField(user, "id", userId);

    // findById를 호출하면 user객체를 반환
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    willDoNothing().given(readStatusRepository).deleteByUserId(userId);

    // when
    // 유저 객체 삭제
    userService.delete(userId);

    // then
    // 데이터 삭제 검증
    then(userRepository).should().findById(userId);
    then(readStatusRepository).should().deleteByUserId(userId);
    then(userRepository).should().delete(user);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 시 예외가 발생해야 합니다.")
  void delete_fail_not_found() {
    // given
    User user = new User("김러키", "lucky@google.com", "123asd");
    //유저 식별자를 받을 수 없으니 하나 생성
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");
    // 없음 예외
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when,then
    // delete실행시 없음 예외
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}