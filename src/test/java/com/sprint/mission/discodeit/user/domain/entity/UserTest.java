package com.sprint.mission.discodeit.user.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User Entity Test")
class UserTest {

    @Test
    @DisplayName("User 생성 - 유효한 값을 입력하면 User 객체가 생성된다.")
    void createUser_success() {
        // given
        String username = "testuser";
        String email = "test@test.com";
        UserRole role = UserRole.USER;

        // when
        User user = User.builder()
                .username(username)
                .email(email)
                .role(role)
                .build();

        // then
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getProfile()).isNull();
    }

    @Test
    @DisplayName("User 생성 - username이 없으면 IllegalArgumentException이 발생한다.")
    void createUser_fail_missingUsername() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.builder()
                        .username("") // empty
                        .email("test@test.com")
                        .role(UserRole.USER)
                        .build())
                .withMessageContaining("username is necessary");
    }

    @Test
    @DisplayName("updateUsername - 새로운 이름을 입력하면 username이 변경되고 true를 반환한다.")
    void updateUsername_success() {
        User user = User.builder().username("old").email("test@test.com").role(UserRole.USER).build();
        user.updateUsername("new", value -> {});

        assertThat(user.getUsername()).isEqualTo("new");
    }

    @Test
    @DisplayName("updateUsername - 기존과 동일한 이름을 입력하면 변경되지 않고 false를 반환한다.")
    void updateUsername_sameValue() {
        User user = User.builder().username("same").email("test@test.com").role(UserRole.USER).build();
        user.updateUsername("same", value -> {});

        assertThat(user.getUsername()).isEqualTo("same");
    }

    @Test
    @DisplayName("updateProfile - 새로운 프로필 사진을 입력하면 profile이 변경되고 true를 반환한다.")
    void updateProfile_success() {
        User user = User.builder().username("test").email("test@test.com").role(UserRole.USER).build();
        BinaryContent newProfile = mock(BinaryContent.class);
        
        user.updateProfile(newProfile);

        assertThat(user.getProfile()).isEqualTo(newProfile);
    }
}
