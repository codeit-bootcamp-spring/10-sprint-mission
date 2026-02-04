package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/*
Given-When-Then (준비 - 실행 - 검증)
    Given : 테스트에 필요한 데이터와 가짜 객체의 행동을 정의
    When : 실제로 테스트할 메서드를 호출
    Then : 결과가 예상과 맞는지 확인
 */

/*
단위 테스트
    단위 테스트는 하나의 모듈읠 기준으로 독립적으로 진행되는 가장 작은 단위의 테스트이다.
    하나의 모듈이란 각 계층에서의 하나의 기능 또는 메서드로 이해할 수 있다.
    하나의 기능이 올바르게 동작하는지를 독립적으로 테스트하는 것이다.
 */

@ExtendWith(MockitoExtension.class)
// @ExtendWith -> 단위 테스트에 공통적으로 사용할 확장 기능을 선언해주는 역할을 한다. 인자로 확장할 Extension을 명시하면 된다.
// JUnit5와 Mockito를 연동해 테스트를 진행할 경우에는 MockitoExtension.class를 사용한다.
public class BasicUserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @InjectMocks
    private BasicUserService basicUserService;

    @Nested
    @DisplayName("Creat(회원가입)")
    class CreateTest {

        @Test
        @DisplayName("성공: 중복되지 않은 정보로 요청 시 유저 생성됨")
        void create_success()  {
            // Given
            UserCreateRequest request = new UserCreateRequest("testUser", "test@gmail.com", "password123", null);

            // 중복 체크
            // Stubbing - 모의 객체 생성 및 모의 객체의 동작을 지정하는 것
            given(userRepository.existsByUsername(request.username())).willReturn(false);
            given(userRepository.existsByEmail(request.email())).willReturn(false);

            // 저장될 유저 객체
            User savedUser = new User(request.username(), request.email(), request.password(), null);
            given(userRepository.save(any(User.class))).willReturn(savedUser);
            // userRepository.save()에 어떤 User 객체가 들어오든, 무조건 savedUser를 반환

            // When
            UserResponse response = basicUserService.create(request);

            // Then
            assertThat(response.username()).isEqualTo("testUser");
            assertThat(response.email()).isEqualTo("test@gmail.com");

            // verify: 실제 저장 메서드가 호출되었는지 확인
            verify(userRepository).save(any(User.class));
            verify(userStatusRepository).save(any(UserStatus.class));

        /*
        단위 테스트에는 2가지 검증 방식이 존재
        1. 상태 검증: 리턴값이 맞는지 확인 (assertThat)
        2. 행위 검증: 로직이 제대로 실행됐는지 확인 (verify)
         */
        }

        @Test
        @DisplayName("실패: 이미 존재하는 사용자 이름이면 예외 발생")
        void create_fail_duplicate_username() {
            // Given
            UserCreateRequest request = new UserCreateRequest("duplicateUser", "new@gmail.com", "pw", null);
            given(userRepository.existsByUsername(request.username())).willReturn(true);

            // When, Then
            assertThatThrownBy(() -> basicUserService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 존재하는 사용자 이름(username)입니다: " + request.username());
        }

        @Test
        @DisplayName("실패: 이미 존재하는 이메일이면 예외 발생")
        void create_fail_duplicate_email() {
            // Given
            UserCreateRequest request = new UserCreateRequest("newUser", "duplicate@gmail.com", "pw", null);
            given(userRepository.existsByEmail(request.email())).willReturn(true);

            // When, Then
            assertThatThrownBy(() -> basicUserService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 사용중인 이메일(email)입니다: " + request.email());
        }
    }

    @Nested
    @DisplayName("유저 조회 (FindById)")
    class FindByIdTest {

        @Test
        @DisplayName("성공: 존재하는 id로 조회 시 유저 정보를 반환")
        void findById_success() {
            // Given
            UUID userId = UUID.randomUUID();
            User user = new User("existUser", "exist@email.com", "pw", null);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // When
            UserResponse response = basicUserService.find(userId);

            // Then
            assertThat(response.username()).isEqualTo("existUser");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 id로 조회 시 예외 발생")
        void findById_fail_not_found() {
            // Given
            UUID userId = UUID.randomUUID();
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When, Then
            assertThatThrownBy(() -> basicUserService.find(userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("해당 id의 유저가 존재하지 않습니다. (userId: " + userId + " )");

            // hasMessage -> 예외 메시지가 기대하는 문자열과 정확히 일치하는지 확인
            // hasMessageContaining -> 예외 메시지에 특정 문자열이 포함되어 있는지 확인
        }
    }
}
