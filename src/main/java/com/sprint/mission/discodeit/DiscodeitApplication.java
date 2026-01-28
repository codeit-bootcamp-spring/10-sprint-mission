package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(DiscodeitApplication.class, args);

        BasicChannelService channelService = ac.getBean(BasicChannelService.class);
        BasicUserService userService = ac.getBean(BasicUserService.class);
        BasicMessageService messageService = ac.getBean(BasicMessageService.class);

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
}
