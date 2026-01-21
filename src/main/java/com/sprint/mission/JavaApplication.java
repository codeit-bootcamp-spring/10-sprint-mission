package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
        System.out.println(message);
    }

    public static void main(String[] args) {

        //JCF*Repository 초기화
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        //File*Repository 초기화
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();

        // 서비스 초기화
        // TODO Basic*Service 구현체를 초기화하세요.
        UserService userService = new BasicUserService(channelRepository, userRepository, messageRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userRepository, messageRepository);
        MessageService messageService = new BasicMessageService(channelRepository, userRepository, messageRepository);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        // 테스트
        // (예외발생) 가입하지 않고 메시지 보내기

        System.out.println("[테스트] 미가입 상태로 메시지 전송 시도");
        messageCreateTest(messageService, channel, user);

        // (성공) 채널 가입 후 메시지 보내기
        channelService.joinChannel(channel.getId(), user.getId());
        System.out.println("[테스트] 가입 후 메시지 전송 시도");
        messageCreateTest(messageService, channel, user);

        System.out.println("============== [테스트 종료] ==============");
    }
}

