package com.sprint.mission.discodeit.event.kafka.dto;

import com.sprint.mission.discodeit.entity.Role;

import java.util.UUID;

public record RoleUpdatedKafkaEvent(
        UUID userId,
        String username,
        Role oldRole,
        Role newRole
) {
}