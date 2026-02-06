//package com.sprint.mission.discodeit.config;
//
//import com.sprint.mission.discodeit.repository.*;
//import com.sprint.mission.discodeit.repository.file.*;
//import com.sprint.mission.discodeit.repository.jcf.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RepositoryConfig {
//
//    @Value("${discodeit.repository.file-directory:.discodeit}")
//    private String fileDirectory;
//
//    // User Repository
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
//    public UserRepository userRepositoryJCF(){
//        return new JCFUserRepository();
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
//    public UserRepository userRepositoryFile(){
//        return new FileUserRepository(fileDirectory);
//    }
//
//    // Channel Repository
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
//    public ChannelRepository channelRepositoryJCF(){
//        return new JCFChannelRepository();
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
//    public ChannelRepository channelRepositoryFile(){
//        return new FileChannelRepository(fileDirectory);
//    }
//
//    // Message Repository
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
//    public MessageRepository messageRepositoryJCF(){
//        return new JCFMessageRepository();
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
//    public MessageRepository messageRepositoryFile(){
//        return new FileMessageRepository(fileDirectory);
//    }
//
//    // BinaryContent Repository
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
//    public BinaryContentRepository binaryContentRepositoryJCF(){
//        return new JCFBinaryContentRepository();
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
//    public BinaryContentRepository binaryContentRepositoryFile(){
//        return new FileBinaryContentRepository(fileDirectory);
//    }
//
//    // ReadStatus Repository
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
//    public ReadStatusRepository readStatusRepositoryJCF(){
//        return new JCFReadStatusRepository();
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
//    public ReadStatusRepository readStatusRepositoryFile(){
//        return new FileReadStatusRepository(fileDirectory);
//    }
//
//    // UserStatus Repository
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
//    public UserStatusRepository userStatusRepositoryJCF(){
//        return new JCFUserStatusRepository();
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
//    public UserStatusRepository userStatusRepositoryFile(){
//        return new FileUserStatusRepository(fileDirectory);
//    }
//}
