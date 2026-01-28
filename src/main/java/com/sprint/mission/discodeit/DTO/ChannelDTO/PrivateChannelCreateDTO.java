package com.sprint.mission.discodeit.DTO.ChannelDTO;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public record PrivateChannelCreateDTO(
        List<User> users
) {
}
