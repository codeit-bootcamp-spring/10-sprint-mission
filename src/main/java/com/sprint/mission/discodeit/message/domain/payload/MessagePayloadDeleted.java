package com.sprint.mission.discodeit.message.domain.payload;

import com.sprint.mission.discodeit.common.payload.marker.PayloadDeletedMarker;
import java.util.UUID;

public record MessagePayloadDeleted(UUID id) implements PayloadDeletedMarker {

}
