package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

public interface DomainService<T, C, U> {
    T create(C model);

    T find(UUID id);

    List<T> findAll();

    T update(U model);

    void delete(UUID id);
}
