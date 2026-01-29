package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class BinaryContent {
    private final UUID id = UUID.randomUUID();
    private final long createdAt = Instant.now().getEpochSecond();
    private final String url;
}
