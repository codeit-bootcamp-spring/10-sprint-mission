package com.sprint.mission.discodeit.service.listener;

import java.util.UUID;

public interface UserLifecycleListener {
    void onUserDelete(UUID userId);
}