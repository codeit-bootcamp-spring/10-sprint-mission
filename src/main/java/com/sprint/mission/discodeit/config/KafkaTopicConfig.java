package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.event.kafka.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic messageCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.MESSAGE_CREATED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic roleUpdatedTopic() {
        return TopicBuilder.name(KafkaTopics.ROLE_UPDATED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic s3UploadFailedTopic() {
        return TopicBuilder.name(KafkaTopics.S3_UPLOAD_FAILED).partitions(1).replicas(1).build();
    }
}
