package com.sprint.mission.discodeit.dto.channeldto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateDTO(
    @JsonAlias("participantIds")
    @NotNull
    List<UUID> users
) {

}
