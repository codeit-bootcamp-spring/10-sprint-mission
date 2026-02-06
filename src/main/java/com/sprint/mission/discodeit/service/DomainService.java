package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface DomainService<F, C, U> {
    F create(C model);

    F find(UUID id);

    F update(U model);

    void delete(UUID id);
}
