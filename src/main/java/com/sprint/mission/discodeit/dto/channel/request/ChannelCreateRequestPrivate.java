package com.sprint.mission.discodeit.dto.channel.request;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;

// Private Channel은 name, descriptions 없어야 함
public record ChannelCreateRequestPrivate(
        List<User> users
) {}
