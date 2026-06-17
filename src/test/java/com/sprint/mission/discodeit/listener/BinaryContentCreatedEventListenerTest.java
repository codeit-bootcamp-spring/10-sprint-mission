package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.manager.BinaryContentStorageManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BinaryContentCreatedEventListenerTest {

    @Mock
    private BinaryContentStorageManager binaryContentStorageManager;

    @InjectMocks
    private BinaryContentCreatedEventListener listener;

    @Test
    @DisplayName("이벤트 수신 시 StorageManager.store(id, bytes)에 그대로 위임한다.")
    void handle_delegatesToStorageManager() {
        // given
        UUID contentId = UUID.randomUUID();
        byte[] bytes = "hello".getBytes();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(contentId, bytes);

        // when
        listener.handleBinaryContentCreated(event);

        // then
        verify(binaryContentStorageManager).store(contentId, bytes);
    }
}
