package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        User user = createUserTest(userService);
        Channel channel = createChannelTest(channelService, user);
        CreateMessageTest(messageService, channel, user);
    }

    private static User createUserTest(UserService userService) {
        User user = userService.createUser("테스터", "tester@test.com");
        System.out.println("회원 생성");
        System.out.println("  • " + user);
        return user;
    }

    private static Channel createChannelTest(ChannelService channelService, User owner) {
        Channel channel = channelService.createChannel("테스트 채널", owner.getId());
        System.out.println("채널 생성");
        System.out.println("  • " + channel);
        return channel;
    }

    private static void CreateMessageTest(MessageService messageService, Channel channel, User user) {
        messageService.createMessage(channel.getId(), user.getId(), "첫 번째 테스트 메시지");
        messageService.createMessage(channel.getId(), user.getId(), "두 번째 테스트 메시지");

        System.out.println("메시지 생성");
        messageService.readMessagesByChannelId(channel.getId())
                .forEach(m -> System.out.println("  • " + m));
        messageService.findMessagesByChannelId(channel.getId())
                .forEach(m -> System.out.println("  • " + m));
    }
}
