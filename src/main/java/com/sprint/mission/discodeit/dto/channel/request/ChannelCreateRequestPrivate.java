package com.sprint.mission.discodeit.dto.channel.request;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public record ChannelCreateRequestPrivate(
        String name,
        List<User> users,
        String descriptions
) {}
