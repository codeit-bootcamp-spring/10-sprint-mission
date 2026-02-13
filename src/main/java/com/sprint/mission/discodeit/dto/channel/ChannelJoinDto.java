package com.sprint.mission.discodeit.dto.channel;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChannelJoinDto {
    private List<UUID> userList = new ArrayList<>();
}
