package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.DomainRepository;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

public abstract class BasicDomainService<T> {
    protected final String ID_NOT_FOUND = "%s with id %s not found";

    protected <R> R findEntityById(UUID id, String type, DomainRepository<R> repository) throws IOException, ClassNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ID_NOT_FOUND.formatted(type, id)));
    }

    protected <R> void deleteIfExist(UUID id, String type, DomainRepository<R> repository) throws IOException {
        if (id == null) {
            return;
        }
        if (!repository.existsById(id)) {
            throw new NoSuchElementException(ID_NOT_FOUND.formatted(type, id));
        }
        repository.deleteById(id);
    }

    protected abstract T findById(UUID id) throws IOException, ClassNotFoundException;
}
