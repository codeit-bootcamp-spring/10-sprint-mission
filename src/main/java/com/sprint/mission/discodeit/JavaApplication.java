package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;

public class JavaApplication {
    public static void main(String[] args) {
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();
        ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();
        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();

        UserMapper userMapper = new UserMapper();
        ChannelMapper channelMapper = new ChannelMapper();
        MessageMapper messageMapper = new MessageMapper();
        ReadStatusMapper readStatusMapper = new ReadStatusMapper();
        UserStatusMapper userStatusMapper = new UserStatusMapper();

        UserService userService = new BasicUserService(userRepository, userStatusRepository, binaryContentRepository, channelRepository, messageRepository, userMapper);
        ChannelService channelService = new BasicChannelService(userRepository, channelRepository, messageRepository, readStatusRepository, channelMapper);
        MessageService messageService = new BasicMessageService(userRepository, channelRepository, messageRepository, binaryContentRepository, messageMapper);
        
        BasicUserStatusService userStatusService = new BasicUserStatusService(userStatusRepository, userRepository, userStatusMapper);
        BasicReadStatusService readStatusService = new BasicReadStatusService(readStatusRepository, userRepository, channelRepository, readStatusMapper);
        BasicAuthService authService = new BasicAuthService(userRepository, userStatusRepository);

        DiscodeitApplication app = new DiscodeitApplication();

        System.out.println("=== JavaApplication (수동 초기화) 테스트 ===");
        app.runTest(userService, channelService, messageService, authService, readStatusService, userStatusService);
    }
}
