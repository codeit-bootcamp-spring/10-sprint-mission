package com.sprint.mission.discodeit.event.kafka;

/**
 * Kafka 토픽 이름 상수. producer/consumer/config가 공통으로 참조한다.
 */
public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String MESSAGE_CREATED = "discodeit.MessageCreatedEvent";
    public static final String ROLE_UPDATED = "discodeit.RoleUpdatedEvent";
    public static final String S3_UPLOAD_FAILED = "discodeit.S3UploadFailedEvent";
    public static final String SSE_FANOUT = "discodeit.sse.fanout";
}
