package com.sprint.mission.discodeit.dto.channeldto;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelUpdateRequestDTO(
    @NotNull
    String newName,
    @NotNull
    String newDescription
) {

}
