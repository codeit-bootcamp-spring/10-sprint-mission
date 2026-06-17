package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.manager.BinaryContentStorageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

    private final BinaryContentStorageManager binaryContentStorageManager;

    @Async("ioTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
        binaryContentStorageManager.store(event.binaryContentId(), event.bytes());
    }
}
