package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Set;
import java.util.UUID;

public interface ChannelRepository {
    public void fileSave(Set<Channel> channels);
    public Set<Channel> fileLoad();
    public void fileDelete(UUID id);
}
