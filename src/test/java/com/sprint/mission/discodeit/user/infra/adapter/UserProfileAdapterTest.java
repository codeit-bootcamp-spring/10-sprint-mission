package com.sprint.mission.discodeit.user.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("UserProfileAdapter Test")
@ExtendWith(MockitoExtension.class)
class UserProfileAdapterTest {

    @Mock
    private EntityReferenceSupplier<BinaryContent> supplier;

    @InjectMocks
    private UserProfileAdapter userProfileAdapter;

    @Test
    @DisplayName("getProxyOrThrow - 유효한 프로필 ID를 요청하면 연관된 BinaryContent를 반환한다.")
    void getProxyOrThrow_success() {
        // given
        UUID profileId = UUID.randomUUID();
        BinaryContent expectedContent = BinaryContent.builder()
                .fileName("test_123.png")
                .contentType("image/png")
                .size(1024L)
                .build();
        
        given(supplier.getProxy(profileId)).willReturn(expectedContent);

        // when
        BinaryContent actualContent = userProfileAdapter.getProxyOrThrow(profileId);

        // then
        assertThat(actualContent).isEqualTo(expectedContent);
    }

    @Test
    @DisplayName("getProxyOrThrow - 존재하지 않는 프로필 ID를 요청하면 예외가 발생한다.")
    void getProxyOrThrow_fail_notFound() {
        // given
        UUID profileId = UUID.randomUUID();
        doThrow(new RuntimeException("Not Found")).when(supplier).existsOrThrow(profileId);

        // when & then
        assertThatThrownBy(() -> userProfileAdapter.getProxyOrThrow(profileId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not Found");
    }
}
