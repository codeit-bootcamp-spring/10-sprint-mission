package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RepositoryProperties.class)
@RequiredArgsConstructor
public class RepositoryConfig {

    private final RepositoryProperties props;

    // =========================
    // JCF repositories
    // =========================
    @Bean
    @ConditionalOnProperty(
            prefix = "discodeit.repository",
            name = "type",
            havingValue = "jcf",
            matchIfMissing = true
    )
    public UserRepository userRepositoryJcf() {
        return new JCFUserRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public ChannelRepository channelRepositoryJcf() {
        return new JCFChannelRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public MessageRepository messageRepositoryJcf() {
        return new JCFMessageRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public UserStatusRepository userStatusRepositoryJcf() {
        return new JCFUserStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public ReadStatusRepository readStatusRepositoryJcf() {
        return new JCFReadStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
    public BinaryContentRepository binaryContentRepositoryJcf() {
        return new JCFBinaryContentRepository();
    }
}