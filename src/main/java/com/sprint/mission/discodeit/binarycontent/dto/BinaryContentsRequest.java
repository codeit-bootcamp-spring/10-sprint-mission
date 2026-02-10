package com.sprint.mission.discodeit.binarycontent.dto;

import java.util.List;
import java.util.UUID;

public record BinaryContentsRequest(
        List<UUID> contentIds
) {}
