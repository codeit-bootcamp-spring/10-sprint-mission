package com.sprint.mission.discodeit.readstatus.application.supplier;

import com.sprint.mission.discodeit.readstatus.infra.repository.ReadStatusRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadStatusReferenceQuerySupplier {

  private final ReadStatusRepository repository;

  public List<UUID> findUserIdsByChannelIdAndNotificationEnabledIsTrue(UUID channelId) {
    return repository.findUserIdsByChannel_IdAndNotificationEnabledIsTrue(channelId);
  }
}
