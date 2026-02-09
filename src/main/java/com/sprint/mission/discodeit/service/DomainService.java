package com.sprint.mission.discodeit.service;

import java.io.IOException;
import java.util.UUID;

public interface DomainService<F, C, U> {
    F create(C model) throws IOException, ClassNotFoundException;

    F find(UUID id) throws IOException, ClassNotFoundException;

    F update(U model) throws IOException, ClassNotFoundException;

    void delete(UUID id) throws IOException, ClassNotFoundException;
}
