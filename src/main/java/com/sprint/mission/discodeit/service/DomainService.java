package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface DomainService<F, C, U> {
    F create(C dto);

    F find(UUID id);

    F update(U dto);

    void delete(UUID id);
}
