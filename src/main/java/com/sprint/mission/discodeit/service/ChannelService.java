package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.util.*;

public interface ChannelService {

    Channel channelCreate(String channelName);

    Channel channelFind(UUID channelId);

    List<Channel> channelFindAll();

    Channel channelMemberAdd(UUID ChannelId, User user);

    Channel channelMemberRemove(UUID channelId, UUID userId);

    Channel channelNameUpdate(UUID id, String channelName);

    Channel channelDelete(UUID channelId);
}


