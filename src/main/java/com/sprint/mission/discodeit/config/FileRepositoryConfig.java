package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
public class FileRepositoryConfig {

    @Bean
    public UserRepository userRepository(Path baseDir) {
        return new FileUserRepository(baseDir);
    }

    @Bean
    public ChannelRepository channelRepository(Path baseDir) {
        return new FileChannelRepository(baseDir);
    }

    @Bean
    public MessageRepository messageRepository(Path baseDir) {
        return new FileMessageRepository(baseDir);
    }

    @Bean
    public ReadStatusRepository readStatusRepository(Path baseDir) {
        return new FileReadStatusRepository(baseDir);
    }

    @Bean
    public UserStatusRepository userStatusRepository(Path baseDir) {
        return new FileUserStatusRepository(baseDir);
    }

    @Bean
    public BinaryContentRepository binaryContentRepository(Path baseDir) {
        return new FileBinaryContentRepository(baseDir);
    }
}
