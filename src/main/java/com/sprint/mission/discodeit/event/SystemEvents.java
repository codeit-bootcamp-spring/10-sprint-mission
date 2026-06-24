package com.sprint.mission.discodeit.event;

public final class SystemEvents {
    private SystemEvents() {}

    public record AsyncErrorAlarm(
            String requestId,
            String methodName,
            String errorMessage
    ) {}
}
