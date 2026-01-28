package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class JavaApplication {
    static User setupUser(UserService userService) {
        User user = userService.create(
            "woody",
            "woody1234",
            "woody@codeit.com",
            "000-0000-0000"
        );

        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create("공지");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create(author.getId(), "안녕하세요.", channel.getId());
        System.out.println("메시지 생성: " + message.getId());
    }


    public static void main(String[] args) {
//        ServiceFactory serviceFactory = new ServiceFactory(ServiceType.JCF);
////        ServiceFactory serviceFactory = new ServiceFactory(ServiceType.FILE);
//
//        UserService userService = serviceFactory.userService();
//        ChannelService channelService = serviceFactory.channelService();
//        MessageService messageService = serviceFactory.messageService();
//
//        // 셋업
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        channelService.addUser(channel.getId(), user.getId());
//
//        // 테스트
//        messageCreateTest(messageService, channel, user);
    }


}
