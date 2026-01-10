package com.sprint.mission.descodeit;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;
import com.sprint.mission.descodeit.service.jcf.JCFChannelService;
import com.sprint.mission.descodeit.service.jcf.JCFMessageService;
import com.sprint.mission.descodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args){
        UserService userService = new JCFUserService();
        MessageService messageService = new JCFMessageService();
        ChannelService channelService = new JCFChannelService();

        // 유저 생성
        User user1 = new User("김현재", "면재", 27, "남");
        userService.create(user1);

        //채널 생성
        Channel channel1 = new Channel("스프린트");
        channelService.create(channel1);

        // 메시지 생성
        Message message1 = new Message(user1, "안녕하세요", channel1);
        messageService.create(message1);
        Message message2 = new Message(user1, "안녕히 계세요", channel1);
        messageService.create(message2);

        // 조회
        userService.findUser(user1.getId());
        userService.findAllUsers();

        channelService.findCannel(channel1.getId());
        channelService.findAllChannel();

        messageService.findMessages(message1.getId());
        messageService.findMessages(message2.getId());
        messageService.findAllMessages();

        System.out.println("");

        // 수정 & 조회
        userService.update(user1.getId(), "현재");
        userService.findUser(user1.getId());

        channelService.update(channel1.getId(), "코드잇");
        channelService.findCannel(channel1.getId());

        messageService.update(message1.getId(), "Hello");
        messageService.findAllMessages();

        System.out.println("");

        //삭제 & 조회
        userService.delete(user1.getId());
        userService.findUser(user1.getId());

        channelService.delete(channel1.getId());
        channelService.findCannel(channel1.getId());

        messageService.delete(message1.getId());
        messageService.findMessages(message1.getId());



    }


}
