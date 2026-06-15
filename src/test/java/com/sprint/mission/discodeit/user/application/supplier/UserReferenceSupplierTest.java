package com.sprint.mission.discodeit.user.application.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.domain.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.domain.exception.UserException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UserReferenceSupplier Test")
@ExtendWith(MockitoExtension.class)
class UserReferenceSupplierTest {

    @Mock
    private DomainRepository<User> userRepository;

    @InjectMocks
    private UserReferenceSupplier supplier;

    @Test
    @DisplayName("getProxy - 존재하는 유저 ID를 입력하면 연관된 User를 반환한다. (Success Case)")
    void getProxy_success() {
        // given
        UUID id = UUID.randomUUID();
        User expectedUser = User.builder().username("testuser").email("test@test.com").role(UserRole.USER).build();
        ReflectionTestUtils.setField(expectedUser, "id", id);
        given(userRepository.getReferenceById(id)).willReturn(expectedUser);

        // when
        User actualUser = supplier.getProxy(id);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("existsOrThrow - 존재하지 않는 유저 ID를 입력하면 예외가 발생한다. (Fail Case)")
    void existsOrThrow_fail_notFound() {
        // given
        UUID id = UUID.randomUUID();
        given(userRepository.existsById(id)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> supplier.existsOrThrow(id))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USERID_NOT_FOUND);
    }

    @Test
    @DisplayName("existsOrThrow - 존재하지 않는 유저 ID 리스트를 입력하면 예외가 발생한다. (Fail Case)")
    void existsOrThrow_collection_fail_notFound() {
        // given
        java.util.List<UUID> ids = java.util.List.of(UUID.randomUUID(), UUID.randomUUID());
        given(userRepository.filterExistingIds(ids)).willReturn(java.util.List.of());

        // when & then
        assertThatThrownBy(() -> supplier.existsOrThrow(ids))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USERID_NOT_FOUND);
    }
}
