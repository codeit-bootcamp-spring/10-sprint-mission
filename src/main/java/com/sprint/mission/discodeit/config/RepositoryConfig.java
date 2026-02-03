package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public UserRepository userRepositoryJcf() {
        return new JCFUserRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public BinaryContentRepository binaryContentRepositoryJcf() {
        return new JCFBinaryContentRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public ReadStatusRepository readStatusRepositoryJcf() {
        return new JCFReadStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public UserStatusRepository userStatusRepositoryJcf() {
        return new JCFUserStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public MessageRepository messageRepositoryJcf() {
        return new JCFMessageRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public ChannelRepository channelRepositoryJcf() {
        return new JCFChannelRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public UserRepository userRepositoryFile() {
        return new FileUserRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public BinaryContentRepository binaryContentRepositoryFile() {
        return new FileBinaryContentRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public ReadStatusRepository readStatusRepositoryFile() {
        return new FileReadStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public UserStatusRepository userStatusRepositoryFile() {
        return new FileUserStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public MessageRepository messageRepositoryFile() {
        return new FileMessageRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public ChannelRepository channelRepositoryFile() {
        return new FileChannelRepository();
    }
}
