package com.sprint.mission.discodeit.dto.channeldto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateDTO(
        @JsonAlias("participantIds") List<UUID> users
) {
}
