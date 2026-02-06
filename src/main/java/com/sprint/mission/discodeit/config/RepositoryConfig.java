package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {


        // YAML 파일의 discodeit.repository.file-directory 값을 읽어옵니다.
        @Value("${discodeit.repository.file-directory}")
        private String fileDirectory;

        // --- 1. User Repository ---
        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
        public UserRepository fileUserRepository() {
            return new FileUserRepository(fileDirectory);
        }

        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
        public UserRepository jcfUserRepository() {
            return new JCFUserRepository();
        }

        // --- 2. UserStatus Repository ---
        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
        public UserStatusRepository fileUserStatusRepository() {
            return new FileUserStatusRepository(fileDirectory);
        }

        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
        public UserStatusRepository jcfUserStatusRepository() {
            return new JCFUserStatusRepository();
        }

        // --- 3. Channel Repository ---
        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
        public ChannelRepository fileChannelRepository() {
            return new FileChannelRepository(fileDirectory);
        }

        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
        public ChannelRepository jcfChannelRepository() {
            return new JCFChannelRepository();
        }

        // --- 4. Message Repository ---
        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
        public MessageRepository fileMessageRepository() {
            return new FileMessageRepository(fileDirectory);
        }

        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
        public MessageRepository jcfMessageRepository() {
            return new JCFMessageRepository();
        }

        // --- 5. BinaryContent Repository ---
        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
        public BinaryContentRepository fileBinaryContentRepository() {
            return new FileBinaryContentRepository(fileDirectory);
        }

        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
        public BinaryContentRepository jcfBinaryContentRepository() {
            return new JCFBinaryContentRepository();
        }

        // --- 6. ReadStatus Repository ---
        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
        public ReadStatusRepository fileReadStatusRepository() {
            return new FileReadStatusRepository(fileDirectory);
        }

        @Bean
        @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
        public ReadStatusRepository jcfReadStatusRepository() {
            return new JCFReadStatusRepository();
        }
}
