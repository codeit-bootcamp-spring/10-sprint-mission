package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.*;

public class JavaApplication {
    public static void main(String[] args) {
        ServiceFactory factory = ServiceFactory.getInstance();

        UserService userService = factory.userService();
        ChannelService channelService = factory.channelService();
        MessageService messageService = factory.messageService();

        User user = userService.create("이정혁");
        Channel channel = channelService.create("일반서버");
        Message message = messageService.create(
                user.getId(),
                channel.getId(),
                "반갑습니다."
        );
        System.out.println(userService.findAll());
        System.out.println(channelService.findAll());
        System.out.println(messageService.findAll());
        System.out.println(userService.findAll());
        System.out.println(channelService.findAll());
        System.out.println(messageService.findAll());
        userService.update(user.getId(), "이정순");
        channelService.update(channel.getId(), "공지서버");
        messageService.update(message.getId(), "모두 반갑습니다.");
        System.out.println(userService.findById(user.getId()));
        System.out.println(channelService.findById(channel.getId()));
        System.out.println(messageService.findById(message.getId()));
        System.out.println(message);
        messageService.delete(message.getId());
        channelService.delete(channel.getId());
        userService.delete(user.getId());
        System.out.println(messageService.findAll());
        System.out.println(channelService.findAll());
        System.out.println(userService.findAll());

    }

}
