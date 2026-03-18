package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.UUID;
import java.util.List;


public interface ChannelService {

  Channel createPublicChannel(String name, String description);

  Channel createPrivateChannel(List<UUID> participantIds);

  Channel findById(UUID id);

  List<Channel> findAllByUserId(UUID userId);

  Channel update(UUID id, String newName, String newDescription);

  void deleteById(UUID id);
}