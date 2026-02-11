package com.sprint.mission.discodeit.dto.request;

import java.util.List;

public record MessageWithBinaryRequest(
        MessageCreateRequest messageRequest,
        List<BinaryContentCreateRequest> binaryRequests
) {}