package com.sprint.mission.discodeit.binarycontent.domain.event;

import java.util.Map;
import java.util.UUID;

public record FileUploadEvent(Map<UUID, byte[]> data) {

}
