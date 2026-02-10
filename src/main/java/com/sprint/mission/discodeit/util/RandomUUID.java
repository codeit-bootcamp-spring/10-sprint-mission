package com.sprint.mission.discodeit.util;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public interface RandomUUID {
    @Component
    @ConditionalOnProperty(
            prefix = "discodeit.uuid",
            name = "type",
            havingValue = "prod"
    )
    class RandomGenerator implements IdGenerator {
        @Override
        public UUID generateId() {
            return UUID.randomUUID();
        }
    }

    @Component
    @ConditionalOnProperty(
            prefix = "discodeit.uuid",
            name = "type",
            havingValue = "dev"
    )
    class FixGenerator implements IdGenerator {
        private final AtomicInteger count = new AtomicInteger();

        @Override
        public UUID generateId() {
            return UUID.fromString(String.format("00000000-0000-0000-0000-%012d", count.incrementAndGet()));
        }
    }
}
