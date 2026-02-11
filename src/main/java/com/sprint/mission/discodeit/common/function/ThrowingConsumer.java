package com.sprint.mission.discodeit.common.function;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    void apply(T t) throws E;

    static <T> Consumer<T> unchecked(ThrowingConsumer<T, Exception> action) {
        return t -> {
            try {
                action.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
