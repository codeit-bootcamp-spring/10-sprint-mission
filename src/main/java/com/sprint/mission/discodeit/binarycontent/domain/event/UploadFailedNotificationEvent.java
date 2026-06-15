package com.sprint.mission.discodeit.binarycontent.domain.event;

import java.util.List;

public record UploadFailedNotificationEvent(String title, List<String> messages) {

}
