package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BinaryContentCreatedEvent event) {
        try {
            binaryContentStorage.put(
                    event.binaryContent().getId(),
                    event.payload().bytes()
            );

            binaryContentService.updateStatus(
                    event.binaryContent().getId(),
                    BinaryContentStatus.SUCCESS
            );
        } catch (Exception e) {
            binaryContentService.updateStatus(
                    event.binaryContent().getId(),
                    BinaryContentStatus.FAIL
            );
        }
    }
}
