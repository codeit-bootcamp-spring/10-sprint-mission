package com.sprint.mission.discodeit.user.application.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.domain.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.domain.exception.UserException;
import com.sprint.mission.discodeit.user.domain.repository.UserQueryReferenceRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("UsernameReferenceSupplier Test")
@ExtendWith(MockitoExtension.class)
class UserQueryReferenceSupplierTest {

    @Mock
    private UserQueryReferenceRepository userQueryReferenceRepository;

    @InjectMocks
    private UserQueryReferenceSupplier supplier;

    @Test
    @DisplayName("getProxyByUsername - 존재하는 username을 입력하면 연관된 User를 반환한다. (Success Case)")
    void getProxyByUsername_success() {
        // given
        String username = "testuser";
        User expectedUser = User.builder().username(username).email("test@test.com").role(UserRole.USER).build();
        given(userQueryReferenceRepository.findByUsername(username)).willReturn(Optional.of(expectedUser));

        // when
        User actualUser = supplier.getProxyByUsername(username);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("getProxyByUsername - 존재하지 않는 username을 입력하면 예외가 발생한다. (Fail Case)")
    void getProxyByUsername_fail_notFound() {
        // given
        String username = "notfound";
        given(userQueryReferenceRepository.findByUsername(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> supplier.getProxyByUsername(username))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USERNAME_NOT_FOUND);
    }
}
