package com.sprint.mission.discodeit.common.function;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {
    R apply(T t) throws E;

    static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R, Exception> action) {
        return t -> {
            try {
                return action.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
