package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChannelDto {

    private UUID id;
    private ChannelType type;
    private String name;
    private String description;
    private List<UserDto> participants = new ArrayList<>();
    private Instant lastMessageAt;
}