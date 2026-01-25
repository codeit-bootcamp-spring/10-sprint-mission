package com.sprint.mission.discodeit.service.listener;

import java.util.*;

public interface ChannelLifecycleListener {
    void onChannelDelete(UUID channelId);
}