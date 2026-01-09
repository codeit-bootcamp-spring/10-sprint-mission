package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
//import com.sprint.mission.discodeit.jcf.JCFChannelService;
//import com.sprint.mission.discodeit.jcf.JCFMessageService;
//import com.sprint.mission.discodeit.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 등록

        // 채널 등록
        //JCFChannelService channelService = new JCFChannelService();
        FileChannelService channelService = new FileChannelService("channel.ser");
        channelService.clear();
        Channel channel1 = new Channel("달선이 채널", false);
        Channel channel2 = new Channel("달룡이 채널", false);
        Channel channel3 = new Channel("달례 채널", false);
        channelService.create(channel1);
        channelService.create(channel2);
        channelService.create(channel3);

        // 유저 등록
        FileUserService userService = new FileUserService("user.ser");
        userService.clear();
        User user1 = new User("달선", UserStatus.ONLINE);
        User user2 = new User("달룡", UserStatus.OFFLINE);
        User user3 = new User("달례", UserStatus.AWAY);
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);

        System.out.println("\n업데이트 확인");
        System.out.println(user1);
        user1.updateStatus(UserStatus.DONOTDISTURB);
        System.out.println(user1);
        System.out.println();

        // 메시지 등록
        FileMessageService messageService = new FileMessageService("message.ser", userService, channelService);
        messageService.clear();
        Message message1 = new Message(user1, channel1.getId(), "언니 보고싶어");
        Message message2 = new Message(user2, channel2.getId(), "얘들아 배고파");
        Message message3 = new Message(user3, channel3.getId(), "엄마 보고싶어");
        messageService.create(message1);
        messageService.create(message2);
        messageService.create(message3);

        // 조회(단건, 다건)
        System.out.println("=====채널 조회=====");
        System.out.println(channelService.read(channel1.getId()));
        System.out.println(channelService.read(channel2.getId()));
        System.out.println(channelService.read(channel3.getId()));
        System.out.println(channelService.readAll());

        System.out.println("=====유저 조회=====");
        System.out.println(userService.read(user1.getId()));
        System.out.println(userService.read(user2.getId()));
        System.out.println(userService.read(user3.getId()));
        System.out.println(userService.readAll());

        System.out.println("=====메시지 조회=====");
        System.out.println(messageService.read(message1.getId()));
        System.out.println(messageService.read(message2.getId()));
        System.out.println(messageService.read(message3.getId()));
        System.out.println(messageService.readAll());


        // 수정
        System.out.println("채널 수정 후");
        channel1.updateName("달선이의 롤 채널");
        channel1.updatePrivate(true);
        channelService.update(channel1);
        System.out.println(channelService.readAll());
//        channelService.delete(channel2.getId()); // 채널 예외 발생


        // 삭제
        System.out.println("유저 삭제 후");
        userService.delete(user3.getId());
        System.out.println(userService.readAll());
        userService.create(user3); // 유저 예외 발생

        // 심화 요구 사항
        System.out.println("심화 요구 사항");
        Message message4 = new Message(user3, channel2.getId(), "오빠 놀쟈!");
        Message message5 = new Message(user3, channel2.getId(), "언니 놀쟈!");
        messageService.create(message4);
        messageService.create(message5);
        System.out.println("보낸 사람 : " + userService.read(message4.getSender().getId()));
        System.out.println("보낸 채널 : " + channelService.read(message4.getChannelId()));
        System.out.println("보낸 메시지 : " + messageService.read(message4.getId()));
        System.out.println("메시지 리스트 : " + user3.getMessages());
    }
}
