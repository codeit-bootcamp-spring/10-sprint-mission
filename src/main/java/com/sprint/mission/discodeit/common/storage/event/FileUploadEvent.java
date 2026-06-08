package com.sprint.mission.discodeit.common.storage.event;

import java.util.Map;
import java.util.UUID;

public record FileUploadEvent(Map<UUID, byte[]> data) {

}
