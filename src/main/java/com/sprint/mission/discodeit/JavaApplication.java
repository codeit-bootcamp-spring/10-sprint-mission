package com.sprint.mission.discodeit;

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
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;

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
//        UserRepository userRepository = new JCFUserRepository();
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        MessageRepository messageRepository = new JCFMessageRepository();

        //File*Repository 초기화
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        // 서비스 초기화
        // TODO Basic*Service 구현체를 초기화하세요.
        UserService userService = new BasicUserService(channelRepository, userRepository, messageRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userRepository, messageRepository);
        MessageService messageService = new BasicMessageService(channelRepository, userRepository, messageRepository);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        channelService.joinChannel(channel.getId(), user.getId());
        // 테스트
        messageCreateTest(messageService, channel, user);

        //조회
        User foundUser = userService.findUserById(user.getId());
        System.out.println("foundUser = " + foundUser);
        List<User> usersByChannel = userService.findUsersByChannel(channel.getId());
        System.out.println("usersByChannel = " + usersByChannel);
        Channel foundChannel = channelService.findChannelById(channel.getId());
        System.out.println("foundChannel = " + foundChannel);
        List<Channel> channelsByUser = channelService.findChannelsByUser(user.getId());
        System.out.println("channelsByUser = " + channelsByUser);
        List<Message> messagesByUser = messageService.findMessagesByUser(user.getId());
        System.out.println("messagesByUser = " + messagesByUser);

        //수정
        userService.update(user.getId(),"woody1234", "hello", "hello@hello.com");
        channelService.update(channel.getId(), "게임", "게임 채널입니다.");
        List<User> allUser = userService.findAllUser();
        System.out.println("allUser = " + allUser);
        List<Channel> channelList = channelService.findAllChannel();
        System.out.println("channelList = " + channelList);

        //삭제
        userService.delete(user.getId(), "woody1234");
        channelService.delete(channel.getId());
        try {
            userService.findUserById(user.getId());
        } catch (Exception e) {
            System.out.println("예외 발생: " + user.getId() + " 유저 삭제 됨");
        }
        try {
            channelService.findChannelById(channel.getId());
        } catch (Exception e) {
            System.out.println("예외 발생: " + channel.getId() + " 채널 삭제 됨");
        }

        System.out.println("============== [테스트 종료] ==============");
    }
}

