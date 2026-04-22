package com.sprint.mission.discodeit.event;

import java.util.Map;
import java.util.UUID;

public record FileUploadEvent(Map<UUID, byte[]> data) {

}
