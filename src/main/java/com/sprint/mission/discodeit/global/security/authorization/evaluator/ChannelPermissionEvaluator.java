package com.sprint.mission.discodeit.global.security.authorization.evaluator;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import org.springframework.stereotype.Component;

@Component
public class ChannelPermissionEvaluator extends AbstractPermissionEvaluator<Channel> {

  public ChannelPermissionEvaluator(ChannelRepository repository) {
    super("Channel", repository);
  }
}
