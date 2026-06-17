package com.sprint.mission.discodeit.storage.manager;

import com.sprint.mission.discodeit.config.RetryConfig;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = {RetryConfig.class, BinaryContentStorageManager.class})
class BinaryContentStorageManagerTest {

    @Autowired
    private BinaryContentStorageManager storageManager;

    @MockitoBean
    private BinaryContentStorage binaryContentStorage;

    @MockitoBean
    private BinaryContentService binaryContentService;

    @MockitoBean
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("CP1: 저장 성공 시 put 1회 호출 후 status를 SUCCESS로 갱신한다.")
    void store_success() {
        // given
        UUID contentId = UUID.randomUUID();
        byte[] bytes = "data".getBytes();
        given(binaryContentStorage.put(eq(contentId), any())).willReturn(contentId);

        // when
        storageManager.store(contentId, bytes);

        // then
        verify(binaryContentStorage, times(1)).put(eq(contentId), any());
        verify(binaryContentService).updateStatus(contentId, BinaryContentStatus.SUCCESS);
    }

    @Test
    @DisplayName("CP4: 일시적 실패 후 재시도하여 3회차에 성공하면 status는 SUCCESS.")
    void store_retriesThenSucceeds() {
        // given
        UUID contentId = UUID.randomUUID();
        byte[] bytes = "data".getBytes();
        given(binaryContentStorage.put(eq(contentId), any()))
                .willThrow(new RuntimeException("일시 실패 1"))
                .willThrow(new RuntimeException("일시 실패 2"))
                .willReturn(contentId);

        // when
        storageManager.store(contentId, bytes);

        // then: 총 3회 시도(maxAttempts=3) 후 성공
        verify(binaryContentStorage, times(3)).put(eq(contentId), any());
        verify(binaryContentService).updateStatus(contentId, BinaryContentStatus.SUCCESS);
    }

    @Test
    @DisplayName("CP4: 모든 재시도가 실패하면 @Recover가 status를 FAIL로 갱신한다.")
    void store_exhaustsRetriesThenRecovers() {
        // given
        UUID contentId = UUID.randomUUID();
        byte[] bytes = "data".getBytes();
        given(binaryContentStorage.put(eq(contentId), any()))
                .willThrow(new RuntimeException("영구 실패"));

        // when: recover가 예외를 흡수하므로 store는 정상 종료한다
        assertThatCode(() -> storageManager.store(contentId, bytes)).doesNotThrowAnyException();

        // then: 정확히 maxAttempts(3)회 시도 후 FAIL 전이, SUCCESS는 호출되지 않음
        verify(binaryContentStorage, times(3)).put(eq(contentId), any());
        verify(binaryContentService).updateStatus(contentId, BinaryContentStatus.FAIL);

        // and: 관리자 통지를 위해 S3UploadFailedEvent를 발행한다 (CP6 핵심 변경)
        ArgumentCaptor<S3UploadFailedEvent> captor = ArgumentCaptor.forClass(S3UploadFailedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        S3UploadFailedEvent published = captor.getValue();
        assertThat(published.binaryContentId()).isEqualTo(contentId);
        assertThat(published.task()).isEqualTo("binaryContentStore");
        assertThat(published.errorMessage()).isEqualTo("영구 실패");
    }
}
