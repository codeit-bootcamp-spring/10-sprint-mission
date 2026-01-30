package com.sprint.mission.discodeit.entity;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class BinaryContent implements Serializable {
    @ToString.Exclude
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private final long createdAt = Instant.now().getEpochSecond();
    @Setter
    private String url;
}
