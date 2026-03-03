package com.sprint.mission.discodeit.entity;

import java.util.function.Consumer;

public abstract class BaseEntity {

    protected  <T> boolean updateIfChanged(T current, T next, Consumer<T> action) {
        if (current == null || current.equals(next)) {
            return false;
        }
        action.accept(next);
        return true;
    }
}
