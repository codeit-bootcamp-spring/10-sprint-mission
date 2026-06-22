package com.sprint.mission.discodeit.readstatus.application.supplier;

import com.sprint.mission.discodeit.global.web.ws.stomp.provider.ChannelSubscribeValidator;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusErrorCode;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusException;
import com.sprint.mission.discodeit.readstatus.domain.repository.ReadStatusReferenceRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadStatusReferenceQuerySupplier implements ChannelSubscribeValidator {

  private final ReadStatusReferenceRepository repository;

  public List<UUID> findUserIdsByChannelIdAndNotificationEnabledIsTrue(UUID channelId) {
    return repository.findUserIdsByChannel_IdAndNotificationEnabledIsTrue(channelId);
  }

  @Override
  public void verifyUserInChannel(UUID channelId, UUID userId) {
    if (repository.existsByChannel_IdAndUser_Id(channelId, userId)) {
      throw new ReadStatusException(ReadStatusErrorCode.READSTATUSID_NOT_FOUND,
          Map.of("detail", "user dont exist in channel, userId: %s, channelId: %s".formatted(userId,
              channelId)));
    }
  }
}
