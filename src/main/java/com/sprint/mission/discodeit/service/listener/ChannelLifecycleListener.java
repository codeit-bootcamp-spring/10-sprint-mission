package com.sprint.mission.discodeit.service.listener;

import java.util.UUID;

public interface ChannelLifecycleListener {
    // 채널이 삭제될 때 호출될 메서드
    void onChannelDelete(UUID channelId);
}