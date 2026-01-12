package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;


public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        User user = userService.createUser("JEON", "ckdgus13@naver.com", "qweasd123");
        User user2 = userService.createUser("Hell", "ckdgus12@naver.com", "qweasd153");
        System.out.println(user.getId());
        User userRead = userService.readUser(user.getId());
        System.out.println(userRead.getId());
        System.out.println(userService.readAllUser());
        System.out.println(user.getUserStatus() + "  " + user.getId());
        userService.updateUser(user.getId(), "Ally", "ckdgus177@gmail.com", "asdfqwer");
        System.out.println(user.getUserStatus() + "  " + user.getId());

//
//        ChannelService channelService = new JCFChannelService();
//        Channel channel = channelService.createChannel(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
//        Channel channel2 = channelService.createChannel(ChannelType.PUBLIC, "공지2", "공지 2채널입니다.");
//        System.out.println(channel.getId());
//        Channel readChannel = channelService.readChannel(channel.getId());
//        System.out.println(readChannel.getId());
//        System.out.println(channelService.readAllChannel());
//
//        MessageService messageService = new JCFMessageService();
//        Message message = messageService.createMessage("안녕하세요.", channel.getId(), user.getId());
//        Message message2 = messageService.createMessage("안녕히가세요.", channel2.getId(), user2.getId());
//        Message messageRead = messageService.readMessage(message.getId());
//        System.out.println(message.getId());
//        System.out.println(messageRead.getId());
//        System.out.println(messageService.readAllMessage());







    }
}
