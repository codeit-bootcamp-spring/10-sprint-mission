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
        User user1 = userService.create("김현재");

        //채널 생성
        Channel channel1 = channelService.create("스프린트");

        // 메시지 생성
        Message message1 = messageService.create(user1, "안녕하세요", channel1);
        Message message2 = messageService.create(user1, "안녕히 계세요", channel1);


        try{
            // 조회
            System.out.println("---유저 조회---");
            userService.findUser(user1.getId());
            userService.findAllUsers();

            System.out.println("---채널 조회---");
            channelService.findCannel(channel1.getId());
            channelService.findAllChannel();

            System.out.println("---메시지 조회---");
            messageService.findMessages(message1.getId());
            messageService.findMessages(message2.getId());
            messageService.findAllMessages();

            System.out.println("");

            // 수정 & 조회
            System.out.println("---유저 수정&조회---");
            userService.update(user1.getId(), "현재");
            userService.findUser(user1.getId());

            System.out.println("---채널 수정&조회---");
            channelService.update(channel1.getId(), "코드잇");
            channelService.findCannel(channel1.getId());

            System.out.println("---메시지 수정&조회---");
            messageService.update(message1.getId(), "Hello");
            messageService.findAllMessages();

            System.out.println("");

            //삭제 & 조회
            System.out.println("---유저 삭제&조회---");
            userService.delete(user1.getId());
            userService.findUser(user1.getId());

            System.out.println("---채널 삭제&조회---");
            channelService.delete(channel1.getId());
            channelService.findCannel(channel1.getId());

            System.out.println("---메시지 삭제&조회---");
            messageService.delete(message1.getId());
            messageService.findMessages(message1.getId());
        } catch (Exception e) {
            System.out.println("조회할 데이터가 없습니다");
        }





    }


}
